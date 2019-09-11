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
    private String os = utils.SystemOps.getOsType().toLowerCase();

    @Test
    void parseReal() {
    }

    @Test
    void parseFakeFile() {
    }

}