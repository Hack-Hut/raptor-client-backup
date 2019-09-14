package stage.master;

<<<<<<< HEAD
import monitors.Audisp;
import monitors.Auditd;
import monitors.BepStep;
import monitors.NullCatcher;
import org.junit.Assume;
=======
import monitors.*;
import org.junit.Assume;
import org.junit.jupiter.api.AfterEach;
>>>>>>> detachedHead
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
<<<<<<< HEAD

=======
import raptorClient.master.Initial;

import static junit.framework.TestCase.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
>>>>>>> detachedHead
import static org.mockito.Mockito.when;

class InitialTest {

    private static final String OS = utils.SystemOps.getOsType().toLowerCase();
    private Initial tester;

<<<<<<< HEAD
=======

>>>>>>> detachedHead
    @Mock
    Audisp audisp;

    @Mock
    Auditd auditd;

    @Mock
    NullCatcher nullCatcher;

    @Mock
    BepStep bepStep;


    @BeforeEach
<<<<<<< HEAD
    public void setUp() throws Exception {
=======
    public void setUp() {
>>>>>>> detachedHead
        MockitoAnnotations.initMocks(this);
    }


    @BeforeEach
    void init(){
        if (OS.contains("linux")){
            String[] cmd = {"ls"};
<<<<<<< HEAD
            tester = new stage.master.Initial(0, "initial", cmd);
        }
        else {
            String[] cmd = {"cmd.exe"};
            tester = new stage.master.Initial(0, "initial", cmd);
        }
    }

    @Test
    void startMonitorsAudispFails() {
        Assume.assumeTrue(OS.contains("linux"));

        when(audisp.start()).thenReturn(false);
        when(auditd.start()).thenReturn(true);

        //tester.setAuditInstance(auditd);
        tester.setAudispInstance(audisp);

        System.out.println(tester.startMonitors());
        System.out.println(1);
        System.out.println(1);
    }

    @Test
    void startMonitorsAuditdFails() {
        Assume.assumeTrue(OS.contains("linux"));
    }

    @Test
    void startMonitorsResourceMonitorFails() {
=======
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
>>>>>>> detachedHead
    }

    @Test
    void startMonitorsNullCatcherFails() {
        Assume.assumeTrue(OS.contains("linux"));
<<<<<<< HEAD
    }

    @Test
    void startMonitorsNothingFails() {
=======
        when(nullCatcher.start()).thenReturn(false);
    }

    @Test
    void startMonitorsNothingFails() throws MonitorFailureException {
        assertTrue(tester.startMonitors());
>>>>>>> detachedHead
    }

    @Test
    void executeBuild() {
<<<<<<< HEAD
=======
        assertTrue(tester.executeBuild());
>>>>>>> detachedHead
    }

    @Test
    void stopMonitorsWhenMonitorsNeverStarted() {
<<<<<<< HEAD
    }

    @Test
    void stopMonitorsWhenAudispNeverStarted() {
        Assume.assumeTrue(OS.contains("linux"));
    }

    @Test
    void stopMonitorsWhenAuditdNeverStarted() {
        Assume.assumeTrue(OS.contains("linux"));
    }

    @Test
    void stopMonitorsWhenNullCatcherdNeverStarted() {
        Assume.assumeTrue(OS.contains("linux"));
    }

    @Test
    void stopMonitorsWhenBepStepNeverStarted() {
        Assume.assumeTrue(OS.contains("linux"));
=======
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
>>>>>>> detachedHead
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