package at.bernd.farka.dynatrace.gradle

import org.gradle.api.Project
import org.gradle.internal.os.OperatingSystem
import org.hamcrest.Matchers
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.mockito.Mockito

import static org.junit.Assert.assertThat
import static org.mockito.Mockito.spy
import static org.mockito.Mockito.when

/**
 * Created by bernd on 28.11.2015.
 */
public class FetchAgentTaskTest {

    @Rule
    public final TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void testDownloadUrl() {
        Project project = org.gradle.testfixtures.ProjectBuilder.builder().build()

        FetchAgentTask task = spy(project.tasks.create("fetch", FetchAgentTask));
        when(task.getPluginExtension()).thenReturn(new PluginExtension(project));
        when(task.getOperatingSystem()).thenReturn(OperatingSystem.WINDOWS);

        def url = task.getDownloadUrl(OperatingSystem.WINDOWS);
        assertThat(url, Matchers.endsWith("dynatrace-agent.msi"))
        assertThat(url, Matchers.containsString(PluginExtension.DEFAULT_VERSION))


        when(task.getOperatingSystem()).thenReturn(OperatingSystem.UNIX)
        url = task.getDownloadUrl(OperatingSystem.UNIX);
        assertThat(url, Matchers.endsWith("dynatrace-agent-unix.jar"))
        assertThat(url, Matchers.containsString(PluginExtension.DEFAULT_VERSION))
    }

    @Test
    public void testCheckChecksumMissing() {
        Project project = org.gradle.testfixtures.ProjectBuilder.builder().build()
        FetchAgentTask task = spy(project.tasks.create("fetch", FetchAgentTask));
        PluginExtension extension = Mockito.spy(new PluginExtension(project))
        when(task.getPluginExtension()).thenReturn(extension);

        final File tmpFolder = folder.newFolder()
        when(extension.downloadFolder).thenReturn(tmpFolder)
        Assert.assertFalse(task.checkChecksum())
    }

    @Test
    public void testCheckChecksumWrong() {
        Project project = org.gradle.testfixtures.ProjectBuilder.builder().build()
        project.apply plugin: "dynatrace-gradle-plugin"
        FetchAgentTask task = spy(project.tasks.create("fetch", FetchAgentTask));
        PluginExtension extension = Mockito.spy(new PluginExtension(project))
        when(task.getPluginExtension()).thenReturn(extension);
        when(task.getOperatingSystem()).thenReturn(OperatingSystem.WINDOWS);

        final File tmpFolder = folder.newFolder()
        new File(tmpFolder, FetchAgentTask.LOCAL_CHECKSUM_FILE_NAME).text = "wrong"
        when(extension.downloadFolder).thenReturn(tmpFolder)
        Assert.assertFalse(task.checkChecksum())
    }

    @Test
    public void testCheckChecksum() {
        Project project = org.gradle.testfixtures.ProjectBuilder.builder().build()
        project.apply plugin: "dynatrace-gradle-plugin"
        FetchAgentTask task = spy(project.tasks.create("fetch", FetchAgentTask));
        PluginExtension extension = Mockito.spy(new PluginExtension(project))
        when(task.getPluginExtension()).thenReturn(extension);

        final File tmpFolder = folder.newFolder()
        new File(tmpFolder, FetchAgentTask.LOCAL_CHECKSUM_FILE_NAME).text = new URL(task.getDownloadUrl(OperatingSystem.current()) + ".md5").text
        when(extension.downloadFolder).thenReturn(tmpFolder)
        Assert.assertTrue(task.checkChecksum())
    }
}
