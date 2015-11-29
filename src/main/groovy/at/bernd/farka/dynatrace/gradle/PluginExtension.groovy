package at.bernd.farka.dynatrace.gradle

import org.gradle.api.Project

/**
 * Created by bernd on 29.11.2015.
 */
class PluginExtension {

    public static final String DEFAULT_VERSION = "6.2"

    public PluginExtension(Project project) {
        this.downloadFolder = new File(project.gradle.gradleUserHomeDir, "dynatrace")
        this.downloadFolder.mkdirs()
    }


    File downloadFolder

    String dynatraceVersion = DEFAULT_VERSION;

}
