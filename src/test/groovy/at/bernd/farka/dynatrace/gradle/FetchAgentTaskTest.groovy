package at.bernd.farka.dynatrace.gradle

import org.gradle.internal.os.OperatingSystem
import org.hamcrest.Matchers
import org.junit.Test
import org.mockito.Mockito

import static org.junit.Assert.assertThat
import static org.mockito.Mockito.when

/**
 * Created by bernd on 28.11.2015.
 */
public class FetchAgentTaskTest {


    @Test
    public void testDownloadUrl() {

        FetchAgentTask task = Mockito.spy(org.gradle.testfixtures.ProjectBuilder.builder().build().tasks.create("fetch", FetchAgentTask));
        when(task.getPluginExtension()).thenReturn(new PluginExtension());
        when(task.getOperatingSystem()).thenReturn(OperatingSystem.WINDOWS);

        def url = task.getDownloadUrl();
        assertThat(url, Matchers.endsWith("dynatrace-agent.msi"))
        assertThat(url, Matchers.containsString(PluginExtension.DEFAULT_VERSION))


        when(task.getOperatingSystem()).thenReturn(OperatingSystem.UNIX)
        url = task.getDownloadUrl();
        assertThat(url, Matchers.endsWith("dynatrace-agent-unix.jar"))
        assertThat(url, Matchers.containsString(PluginExtension.DEFAULT_VERSION))


    }
}
