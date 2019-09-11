package monitors;

import org.junit.Before;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;

import static junit.framework.TestCase.fail;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AuditdTest {

    private static final String AUDIT_LOCATION = "/usr1/auditd.log";
    private Auditd tester;

    @Before
    void beforeMethod() {
        org.junit.Assume.assumeTrue(utils.SystemOps.getOsType().toLowerCase().contains("linux"));
    }

    @BeforeEach
    void setUp() {
        tester = new Auditd();
    }

    @AfterEach
    void tearDown() {
        tester.stop();
    }

    @Test
    void setup() {
        assertTrue(tester.start());
    }

    @Test
    void makeSureSetupClearPreviousLogs() throws IOException {
        File log = new File(AUDIT_LOCATION);
        String[] cmd = {"sudo", "rm", AUDIT_LOCATION};
        System.out.println(utils.Exec.executeCommandGetOutput(cmd));
        System.out.println(log.delete());

        BufferedWriter writer = new BufferedWriter(new FileWriter(AUDIT_LOCATION));
        String searchString = "SEARCH STRING UNIT TEST";
        writer.write(searchString);
        writer.close();

        tester.setup();

        try {
            BufferedReader reader = new BufferedReader(new FileReader(AUDIT_LOCATION));
            String line = reader.readLine();
            while (line != null) {
                if (line.contains(searchString)) {
                    fail("Failed to clear the log file");
                }
                line = reader.readLine();
            }
            reader.close();
        }catch(FileNotFoundException e){
            return;
        }
        fail("Log file was created.");
    }

    @Test
    void start() {

    }

    @Test
    void stop() {
    }

    @Test
    void getExecutables() {
    }

    @Test
    void generateConfigurationFiles() {
    }
}