package at.bernd.farka.dynatrace.gradle

import org.gradle.api.DefaultTask
import org.gradle.internal.os.OperatingSystem

/**
 * Created by bernd on 28.11.2015.
 */
class FetchAgentTask extends DefaultTask {

    private static String URL_TEMPLATE_UNIX = 'http://downloads.dynatracesaas.com/%1$s/dynatrace-agent-unix.jar'
    private static String URL_TEMPLATE_WINDOWS = 'http://downloads.dynatracesaas.com/%1$s/dynatrace-agent.msi'


    public void download() {


    }

    OperatingSystem getOperatingSystem() {
        return OperatingSystem.current();
    }

    PluginExtension getPluginExtension() {
        return project.getExtensions().findByName(DynatraceGradlePlugin.EXTENSION_NAME);
    }

    String getDownloadUrl() {
        def templateString;

        if (getOperatingSystem() == OperatingSystem.WINDOWS) {
            templateString = URL_TEMPLATE_WINDOWS;
        } else {
            templateString = URL_TEMPLATE_UNIX;
        }
        return sprintf(templateString, getPluginExtension().dynatraceVersion)
    }


}
