package at.bernd.farka.dynatrace.gradle

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Assert
import org.junit.Test

/**
 * Created by bernd on 29.11.2015.
 */
class DynatraceGradlePluginTest {

    @Test
    public void testPluginSetup() {

        Project project = ProjectBuilder.builder().build()
        project.apply plugin: 'dynatrace-gradle-plugin'
        def extension = project.extensions.findByName(DynatraceGradlePlugin.EXTENSION_NAME)
        Assert.assertNotNull(extension)
        Assert.assertTrue(extension instanceof PluginExtension)
    }

}
