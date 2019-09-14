package stage.master;

import monitors.*;
import org.junit.Assume;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import raptorClient.master.Initial;

import static junit.framework.TestCase.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.when;

class InitialTest {

    private static final String OS = utils.SystemOps.getOsType().toLowerCase();
    private Initial tester;


    @Mock
    Audisp audisp;

    @Mock
    Auditd auditd;

    @Mock
    NullCatcher nullCatcher;

    @Mock
    BepStep bepStep;


    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }


    @BeforeEach
    void init(){
        if (OS.contains("linux")){
            String[] cmd = {"ls"};
            tester = new Initial(0, "initial", cmd);
        }
        else {
            String[] cmd = {"cmd.exe"};
            tester = new Initial(0, "initial", cmd);
        }
    }

    @AfterEach
    void after() throws InterruptedException {
        // Sometimes auditd can take a while to stop a job.
        // Allowing a grace period makes the tests much more reliable.
        Thread.sleep(100);
        tester.stopMonitors();
        Thread.sleep(200);
    }

    @Test
    void startMonitorsAudispFails() throws MonitorFailureException {
        Assume.assumeTrue(OS.contains("linux"));
        when(audisp.start()).thenReturn(false);
        tester.setAudispInstance(audisp);

        assertTrue(tester.startMonitors());

        tester.stopMonitors();
    }

    @Test
    void startMonitorsAuditdFails() throws MonitorFailureException {
        Assume.assumeTrue(OS.contains("linux"));
        when(auditd.start()).thenReturn(false);
        tester.setAuditInstance(auditd);

        assertTrue(tester.startMonitors());
    }

    @Test
    void startMonitorsNullCatcherFails() {
        Assume.assumeTrue(OS.contains("linux"));
        when(nullCatcher.start()).thenReturn(false);
    }

    @Test
    void startMonitorsNothingFails() throws MonitorFailureException {
        assertTrue(tester.startMonitors());
    }

    @Test
    void executeBuild() {
        assertTrue(tester.executeBuild());
    }

    @Test
    void stopMonitorsWhenMonitorsNeverStarted() {
        assertFalse(tester.stopMonitors());
    }

    @Test
    void stopMonitorsWhenAudispNeverStarted() throws MonitorFailureException {
        Assume.assumeTrue(OS.contains("linux"));
        when(audisp.start()).thenReturn(false);
        tester.setAudispInstance(audisp);
        tester.startMonitors();
        tester.executeBuild();

        assertTrue(tester.stopMonitors());
    }

    @Test
    void stopMonitorsWhenAuditdNeverStarted() throws MonitorFailureException {
        Assume.assumeTrue(OS.contains("linux"));

        when(auditd.start()).thenReturn(false);
        tester.setAuditInstance(auditd);
        tester.startMonitors();
        tester.executeBuild();

        assertTrue(tester.stopMonitors());
    }

    @Test
    void stopMonitorsWhenBepStepNeverStarted() throws MonitorFailureException {
        Assume.assumeTrue(OS.contains("linux"));
        when(bepStep.start()).thenReturn(false);
        tester.startMonitors();
        tester.executeBuild();

        assertTrue(tester.stopMonitors());
    }

    @Test
    void processResultsWhenAuditdNeverStarted() {
        Assume.assumeTrue(OS.contains("linux"));
    }

    @Test
    void processResultsWhenAuditspNeverStarted() {
        Assume.assumeTrue(OS.contains("linux"));
    }

    @Test
    void processResultsWhenAuditorsNeverStarted() {
        Assume.assumeTrue(OS.contains("linux"));
    }

    @Test
    void uploadResults() {
    }
}