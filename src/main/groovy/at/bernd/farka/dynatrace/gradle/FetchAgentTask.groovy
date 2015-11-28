package at.bernd.farka.dynatrace.gradle

import org.gradle.api.DefaultTask
import org.gradle.internal.os.OperatingSystem

/**
 * Created by bernd on 28.11.2015.
 */
class FetchAgentTask extends DefaultTask {


    public void download(){
        OperatingSystem.current()
    }

}
