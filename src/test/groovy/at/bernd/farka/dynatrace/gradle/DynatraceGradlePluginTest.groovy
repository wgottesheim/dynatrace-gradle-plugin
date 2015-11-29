package at.bernd.farka.dynatrace.gradle

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Test

import static org.junit.Assert.assertNotNull
import static org.junit.Assert.assertTrue

/**
 * Created by bernd on 29.11.2015.
 */
class DynatraceGradlePluginTest {

    @Test
    public void testPluginSetup() {

        Project project = ProjectBuilder.builder().build()
        project.apply plugin: 'dynatrace-gradle-plugin'
        PluginExtension extension = project.extensions.findByName(DynatraceGradlePlugin.EXTENSION_NAME)
        assertNotNull(extension)
        assertTrue(extension instanceof PluginExtension)

        FetchAgentTask fetchTask = project.tasks.findByName(DynatraceGradlePlugin.FETCH_AGENT_TASK_NAME)
        assertNotNull(fetchTask)
        assertTrue(fetchTask instanceof FetchAgentTask)

        assertTrue(extension.downloadFolder.exists())
        assertTrue(extension.downloadFolder.isDirectory())
    }

}
