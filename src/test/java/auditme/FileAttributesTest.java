package auditme;

import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class FileAttributesTest {

    private static String cwd = utils.SystemOps.getCWD();
    private static String resources = cwd + "/src/test/resources/auditme/FileAttributes/";
    private static String realFile = resources + "gcc";
    private static String fakeFile = resources + "fake";
    private String os = utils.SystemOps.getOsType().toLowerCase();


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
        if (os.equals("windows")) return;
        FileAttributes tester = new auditme.FileAttributes(realFile);
        tester.populateFileInfo();
        HashMap<String, String> results = tester.getInfo();

        assertEquals("true", results.get("shouldBeProxied"), "should be proxied");
        assertEquals("d41d8cd98f00b204e9800998ecf8427e", results.get("md5"), "md5");
        assertEquals("e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855", results.get("sha256"), "sha256");
    }
}