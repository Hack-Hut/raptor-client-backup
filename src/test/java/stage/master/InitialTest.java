package stage.master;

import monitors.Audisp;
import monitors.Auditd;
import monitors.BepStep;
import monitors.NullCatcher;
import org.junit.Assume;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

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
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }


    @BeforeEach
    void init(){
        if (OS.contains("linux")){
            String[] cmd = {"ls"};
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
    }

    @Test
    void startMonitorsNullCatcherFails() {
        Assume.assumeTrue(OS.contains("linux"));
    }

    @Test
    void startMonitorsNothingFails() {
    }

    @Test
    void executeBuild() {
    }

    @Test
    void stopMonitorsWhenMonitorsNeverStarted() {
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