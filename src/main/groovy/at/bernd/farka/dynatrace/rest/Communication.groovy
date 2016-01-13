package at.bernd.farka.dynatrace.rest

import groovyx.net.http.AuthConfig
import groovyx.net.http.HTTPBuilder
import groovyx.net.http.RESTClient
import org.apache.http.HttpEntity
import org.apache.http.conn.scheme.Scheme
import org.apache.http.conn.ssl.SSLSocketFactory
import org.apache.http.entity.ContentType
import org.codehaus.groovy.runtime.MethodClosure
import org.apache.http.entity.mime.MultipartEntityBuilder
import org.mortbay.util.MultiPartWriter

import javax.net.ssl.HostnameVerifier
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager
import java.security.cert.X509Certificate
import java.security.SecureRandom

/**
 * Created by wolfgang on 12.01.16.
 */
class Communication {

    String protocol
    String host
    String user
    String pass
    int port
    RESTClient dt
    boolean ignoreCert

    def initialize(URL target) {
        dt = new RESTClient(target)
        def ac = new AuthConfig(dt)
        ac.basic(user, pass)
        dt.setAuthConfig(ac)

        if (ignoreCert) {
            println "Accepting insecure SSL certificates - use with caution"
            // accept insecure SSL certificats
            def nullTrustManager = [
                    checkClientTrusted: { chain, authType -> },
                    checkServerTrusted: { chain, authType -> },
                    getAcceptedIssuers: { null }
            ]

            def nullHostnameVerifier = [
                    verify: { hostname, session -> true }
            ]

            def sslContext = SSLContext.getInstance("SSL")
            sslContext.init(null,
                    [new X509TrustManager() {
                        public X509Certificate[] getAcceptedIssuers() { null }

                        public void checkClientTrusted(X509Certificate[] certs, String authType) {}

                        public void checkServerTrusted(X509Certificate[] certs, String authType) {}
                    }] as TrustManager[], new SecureRandom())
            def sf = new SSLSocketFactory(sslContext, SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER)
            def httpsScheme = new Scheme(protocol, sf, port)
            dt.client.connectionManager.schemeRegistry.register(httpsScheme)
        }
    }

    def installPlugin(File plugin) {
        try {
            URL target = new URL("${protocol}://${host}:${port}/rest/management/installjobs")
            initialize(target)
            new UploadPluginCallable(dt.client, target, plugin).call()
        } finally {
            dt.shutdown()
        }
    }
}
