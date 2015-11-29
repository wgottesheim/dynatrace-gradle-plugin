package at.bernd.farka.dynatrace.gradle

import org.gradle.api.DefaultTask
import org.gradle.internal.os.OperatingSystem

/**
 * Created by bernd on 28.11.2015.
 */
class FetchAgentTask extends DefaultTask {

    private static String URL_TEMPLATE = "http://downloads.dynatracesaas.com/6.2/"


    public void download(){
        OperatingSystem.current()
    }

}
