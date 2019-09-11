package monitors;

import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class NullCatcherTest {
    private static final String CWD = new File("").getAbsolutePath();
    private static final String LOG_LOCATION = CWD + "/logs/null-catcher.log";
    private NullCatcher tester;

    @Before
    void beforeMethod() {
        org.junit.Assume.assumeTrue(utils.SystemOps.getOsType().toLowerCase().contains("linux"));
    }

    @BeforeEach
    private void init() {
        tester = new NullCatcher();
    }

    @Test
    void startTestLogFileIsCreated(){
        String[] cmd = {"sudo", "rm", LOG_LOCATION};
        utils.Exec.executeCommandGetOutput(cmd);

        File log = new File(LOG_LOCATION);
        if (log.exists()){
            fail("Test failed to delete the log file at " + LOG_LOCATION);
        }

        tester.start();
        tester.stop();
        assertTrue(log.exists(), "Failed to create the new log file.");
    }


    @RepeatedTest(10)
    void testDevNullCapturesStuff() throws InterruptedException {
        assertTrue(tester.start());
        String[] cmd = {"sudo", CWD + "/resources/write_to_dev_null.sh"};
        utils.Exec.executeCommandGetOutput(cmd);
        Thread.sleep(100);
        try (BufferedReader reader = new BufferedReader(new FileReader("/dev/null"))){
            String line = reader.readLine();
            int lineCount = 0;
            while (line != null) {
                lineCount += 1;
                assertEquals("UNIT TEST", line);
                line = reader.readLine();
            }
            assertEquals(1, lineCount, "line count should be 1");
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            tester.stop();
        }
    }

    @Test
    void testDevNullGetsBackedUpDuringTest(){
        tester.start();
        File devnull = new File("/dev/null.bk");
        assertTrue(devnull.exists());
        tester.stop();
    }

    @Test
    void testDevNullDeletesOldBackupAfterFinished(){
        tester.start();
        tester.stop();
        File devnull = new File("/dev/null.bk");
        assertFalse(devnull.exists());
    }

}