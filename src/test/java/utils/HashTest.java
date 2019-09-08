package utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HashTest {
    private static String cwd = utils.SystemOps.getCWD();
    private static String resources = cwd + "/src/test/resources/Hash";

    @Test
    void md5File(){
        String checksum = Hash.MD5.checksum(resources + "/hashme.txt");
        assertEquals("b026324c6904b2a9cb4b88d6d61c81d1", checksum);
    }

    @Test
    void md5FakeFile(){
        String checksum = Hash.MD5.checksum(resources + "/fake.txt");
        Assertions.assertNull(checksum);
    }

}