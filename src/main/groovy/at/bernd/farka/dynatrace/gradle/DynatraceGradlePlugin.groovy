package at.bernd.farka.dynatrace.gradle


import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Created by bernd on 29.11.2015.
 */
class DynatraceGradlePlugin implements Plugin<Project> {

    public static final String EXTENSION_NAME = "dynatrace"

    @Override
    void apply(Project project) {
        project.extensions.add(EXTENSION_NAME, new PluginExtension())
    }
}
