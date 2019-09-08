package utils;

import org.junit.jupiter.api.Test;

import java.awt.desktop.SystemSleepEvent;

import static org.junit.jupiter.api.Assertions.*;

class FileOperationsTest {

    private static String cwd = utils.SystemOps.getCWD();
    private static String resources = cwd + "/src/test/resources/fileOperations";
    private String os = utils.SystemOps.getOsType().toLowerCase();


    @Test
    void getSymLocation() {
        if (!os.equals("linux")) return;
        String location = utils.FileOperations.getSymLocation(resources + "/test-sym");
        assertEquals("/etc/passwd", location, "failed to get the correct symbolic location");

        location = utils.FileOperations.getSymLocation(resources + "fakefile");
        assertEquals("", location, "failed to get the correct symbolic location");
    }

    @Test
    void exists() {
        String location = resources + "/test-sym";
        assertTrue(utils.FileOperations.exists(location));

        location += "fakeLocation";
        assertFalse(utils.FileOperations.exists(location));
    }

    @Test
    void canRead() {
        String location = resources + "/readable";
        assertTrue(utils.FileOperations.canRead(location));
        assertFalse(utils.FileOperations.canRead(location + "fake file"));
    }

    @Test
    void getBasePath() {
        String location = utils.FileOperations.getBasePath("/ab/c/d");
        assertEquals("d", location);
    }
}