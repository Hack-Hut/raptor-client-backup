package monitors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ResourceMonitorTest {
    private static final String CWD = new File("").getAbsolutePath();
    private static final String LOG_LOCATION = CWD + "/logs/resource-info.csv";
    private static final String OS = utils.SystemOps.getOsType().toLowerCase();
    private ResourceMonitor tester;

    @BeforeEach
    private void init() {
        tester = new ResourceMonitor();
    }

    @Test
    void setupMakeSureLogIsDeleted() {
        if (OS.contains("linux")) {
            String[] cmd = {"touch", LOG_LOCATION};
            utils.Exec.executeCommandGetOutput(cmd);
        }
        else{
            String[] cmd = {"cmd", "/c", "copy", "nul", LOG_LOCATION};
            utils.Exec.executeCommandGetOutput(cmd);
        }
        File log = new File(LOG_LOCATION);
        tester.setup();
        assertFalse(log.exists());
    }

    @Test
    void start() throws InterruptedException {
        tester.start();
        Thread.sleep(100);
        assertTrue(tester.stop());
    }

    @Test
    void startWritesNewLogs() throws InterruptedException {
        if (OS.contains("linux")) {
            String[] cmd = {"sudo", "rm", LOG_LOCATION};
            utils.Exec.executeCommandGetOutput(cmd);
        }
        else{
            String[] cmd = {"cmd", "/c", "del", LOG_LOCATION};
            utils.Exec.executeCommandGetOutput(cmd);
        }

        tester.setup();
        tester.start();
        Thread.sleep(100);
        tester.stop();

        File log = new File(LOG_LOCATION);
        assertTrue(log.exists(), "Resource monitor failed to create new log file");
    }
}