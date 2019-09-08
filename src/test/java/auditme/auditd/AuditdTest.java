package auditme.auditd;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AuditdTest {
    private static String cwd = utils.SystemOps.getCWD();
    private static String resources = cwd + "/src/test/resources/auditme/auditd/";
    private static String realAuditd = resources + "audit.log";
    private static String fakeAuditd = resources + "fake";

    @Test
    void parseReal() {
        Auditd tester = new auditme.auditd.Auditd(realAuditd);
        List<String> expected = new ArrayList<>();
        List<String> results = tester.parse();

        expected.add("/usr/bin/audispd");
        expected.add("/usr/bin/auditd");
        expected.add("/usr/bin/bash");
        expected.add("/usr/bin/date");
        expected.add("/usr/bin/git");
        expected.add("/usr/bin/hostname");
        expected.add("/usr/bin/ldconfig");
        expected.add("/usr/bin/mkdir");
        expected.add("/usr/bin/ps");
        expected.add("/usr/bin/rm");
        expected.add("/usr/bin/sudo");
        expected.add("/usr/lib/jvm/java-11-openjdk/bin/java");

        Collections.sort(expected);
        Collections.sort(results);

        assertEquals(expected, results);
    }

    @Test
    void parseFakeFile() {
        Auditd tester = new auditme.auditd.Auditd(fakeAuditd);
        assertTrue(tester.parse().isEmpty());
    }

}