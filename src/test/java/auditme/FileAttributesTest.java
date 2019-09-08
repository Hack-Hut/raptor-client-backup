package auditme;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FileAttributesTest {

    private static String cwd = utils.SystemOps.getCWD();
    private static String resources = cwd + "/src/test/resources/auditme/FileAttributes/";
    private static String realFile = resources + "gcc";
    private static String fakeFile = resources + "fake";

    @Test
    void populateFileInfoRealFile() {
        FileAttributes tester = new auditme.FileAttributes(realFile);
        assertTrue(tester.populateFileInfo());
    }

    @Test
    void populateFileInfoFakeFile() {
        FileAttributes tester = new auditme.FileAttributes(fakeFile);
        assertFalse(tester.populateFileInfo());
    }

    @Test
    void showFileInfo() {
        FileAttributes tester = new auditme.FileAttributes(realFile);
        tester.populateFileInfo();
        tester.showFileInfo();
    }

    @Test
    void getInfo() {
        
    }
}