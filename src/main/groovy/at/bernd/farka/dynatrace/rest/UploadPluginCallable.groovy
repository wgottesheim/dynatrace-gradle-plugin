package at.bernd.farka.dynatrace.rest

import com.google.common.io.ByteStreams
import org.apache.http.HttpResponse
import org.apache.http.auth.AuthScope
import org.apache.http.auth.UsernamePasswordCredentials
import org.apache.http.client.HttpClient
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpPost
import org.apache.http.impl.client.DefaultHttpClient
import org.gradle.api.GradleException
import org.mozilla.javascript.tools.idswitch.FileBody
import org.apache.http.entity.mime.MultipartEntity
import org.apache.http.entity.mime.content.FileBody

import java.util.concurrent.Callable

/**
 * Created by cwat-bfarka on 11.03.2015.
 */
class UploadPluginCallable implements Callable<Boolean> {
    private HttpGet getLocation;
    private HttpClient httpClient;

    final URL target
    final String protocol
    final String hostName
    final int port
    final File uploadFile

    public UploadPluginCallable( HttpClient httpClient, URL target, File uploadFile ){
        this.httpClient = httpClient
        this.uploadFile = uploadFile
        this.target = target

        deployPlugin();
    }

    private void deployPlugin() {
        HttpPost postRequest = new HttpPost(target.toURI())

        MultipartEntity multipartEntityEntity = new MultipartEntity();
        multipartEntityEntity.addPart("file",new FileBody(uploadFile))
        postRequest.setEntity(new ProgressHttpEntityWrapper(multipartEntityEntity, createCallBack()));

        HttpResponse response = httpClient.execute(postRequest);

        def body = new String(ByteStreams.toByteArray(response.entity.content))
        if(response.statusLine.statusCode != 201){
            throw new GradleException("error @ fixpack deployment " + body)
        }
        def location = response.getHeaders("Location").first().getValue()
        getLocation = new HttpGet(location)
    }

    private ProgressHttpEntityWrapper.ProgressCallback createCallBack(){
        ProgressHttpEntityWrapper.ProgressCallback progressCallback = new ProgressHttpEntityWrapper.ProgressCallback() {
            private long lastProgressLog = System.currentTimeMillis();
            private float lastProgress;

            @Override
            public void progress(float progress) {
                long current = System.currentTimeMillis();
                if (lastProgressLog + 5 * 1000 < current && progress > lastProgress) {
                    lastProgress = progress;
                    lastProgressLog = current;
                    println ("upload progress: " + String.format("%.2f", progress) + "%" );
                }
            }
        };
        return progressCallback;
    }

    @Override
    def Boolean call() throws Exception {
        HttpResponse response = httpClient.execute(getLocation)
        if(response.statusLine.statusCode != 200){
            throw new GradleException("error @ polling fixpack install state!")
        }
        def updateState = new XmlSlurper().parse(response.entity.content)
        if(updateState.isfinished == true && updateState.issuccess == true){
            return true
        }
        if(updateState.isfinished == true && updateState.issuccess == false){
            throw new GradleException("install fixpack failed!")
        }
        return false
    }
}
