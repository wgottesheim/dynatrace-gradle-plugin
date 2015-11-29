package at.bernd.farka.dynatrace.gradle

import com.google.common.io.Files
import org.gradle.api.DefaultTask
import org.gradle.internal.os.OperatingSystem

import java.util.concurrent.TimeUnit
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

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
        final URL downloadUrl = new URL(getDownloadUrl(OperatingSystem.WINDOWS));
        File msiFile = File.createTempFile("dynatrace", "msi");
        msiFile.deleteOnExit();
        logger.info("downloading Dynatrace agent " + downloadUrl.toString())
        //msiexec /a f:\zenworks\zfdagent.msi /qb TARGETDIR=c:\zfd701
        downloadUrl.withInputStream { input ->
            msiFile.withOutputStream { out ->
                out << input
            }
        }
        File tmpFolder = File.createTempDir("dynatrace", "agents")
        logger.info("extracting Dynatrace agent");
        ProcessBuilder builder = new ProcessBuilder();
        builder.command("msiexec", "/a", msiFile.getAbsolutePath(), "/qb", "TARGETDIR=${tmpFolder.getAbsolutePath()}")
        Process process = builder.start()
        process.waitForOrKill(TimeUnit.MINUTES.toMillis(1))

        final File targetDir = getPluginExtension().getDownloadFolder()
        final File libFolder = new File(targetDir, "lib");
        final File libFolder64 = new File(targetDir, "lib64");
        libFolder.mkdir()
        libFolder64.mkdir()
        Files.copy(new File(tmpFolder, "agent/lib/dtagent.dll"), new File(libFolder, "dtagent.dll"))
        Files.copy(new File(tmpFolder, "agent/lib64/dtagent.dll"), new File(libFolder64, "dtagent.dll"))

    }

    public void downloadLinux() {
        final URL downloadUrl = new URL(getDownloadUrl(OperatingSystem.UNIX));
        downloadUrl.withInputStream { input ->
            final ZipInputStream zis = new ZipInputStream(input)
            ZipEntry entry;
            final File targetDir = getPluginExtension().getDownloadFolder()
            final File libFolder = new File(targetDir, "lib");
            final File libFolder64 = new File(targetDir, "lib64");
            libFolder.mkdir()
            libFolder64.mkdir()
            while ((entry = zis.nextEntry) != null) {
                println entry.name
                if ("agent/linux-x86-32/agent/lib/libdtagent.so".equals(entry.name)) {
                    new File(libFolder, "libdtagent.so").withOutputStream { fos ->
                        fos << zis
                    }
                } else if ("agent/linux-x86-64/agent/lib64/libdtagent.so".equals(entry.name)) {
                    new File(libFolder64, "libdtagent.so").withOutputStream { fos ->
                        fos << zis
                    }
                }

            }
        }
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
