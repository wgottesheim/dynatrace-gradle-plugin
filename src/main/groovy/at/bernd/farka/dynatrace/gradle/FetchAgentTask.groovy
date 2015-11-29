package at.bernd.farka.dynatrace.gradle

import org.gradle.api.DefaultTask
import org.gradle.internal.os.OperatingSystem

/**
 * Created by bernd on 28.11.2015.
 */
class FetchAgentTask extends DefaultTask {

    private static String URL_TEMPLATE_UNIX = 'http://downloads.dynatracesaas.com/%1$s/dynatrace-agent-unix.jar'
    private static String URL_TEMPLATE_WINDOWS = 'http://downloads.dynatracesaas.com/%1$s/dynatrace-agent.msi'
    public static final String LOCAL_CHECKSUM_FILE_NAME = "checksum.md5";

    public FetchAgentTask() {
        getOutputs().upToDateWhen {
            checkChecksum()
        }
    }


    public void download() {
        if (OperatingSystem.current() == OperatingSystem.WINDOWS) {
            downloadWindows();
        } else {
            downloadLinux();
        }
    }

    public void downloadWindows() {

    }

    public void downloadLinux() {

    }

    boolean checkChecksum() {
        final File downloadFolder = getPluginExtension().downloadFolder
        final File checkSumFile = new File(downloadFolder, LOCAL_CHECKSUM_FILE_NAME)
        if (checkSumFile.exists()) {
            final String remoteCheckSum = new URL(getDownloadUrl(getOperatingSystem()) + ".md5").text;
            final String localCheckSum = checkSumFile.text
            return remoteCheckSum.trim().equals(localCheckSum.trim());
        }
        return false;
    }

    OperatingSystem getOperatingSystem() {
        return OperatingSystem.current();
    }

    PluginExtension getPluginExtension() {
        return project.getExtensions().findByName(DynatraceGradlePlugin.EXTENSION_NAME);
    }

    String getDownloadUrl(OperatingSystem os) {
        def templateString;

        if (os == OperatingSystem.WINDOWS) {
            templateString = URL_TEMPLATE_WINDOWS;
        } else {
            templateString = URL_TEMPLATE_UNIX;
        }
        return sprintf(templateString, getPluginExtension().dynatraceVersion)
    }


}
