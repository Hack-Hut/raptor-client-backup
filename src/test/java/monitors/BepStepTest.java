package monitors;

import org.junit.Before;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BepStepTest {

    @Before
    void beforeMethod() {
        org.junit.Assume.assumeTrue(utils.SystemOps.getOsType().toLowerCase().contains("linux"));
    }

    @Test
    void testInvalidID(){
        BepStep bepStep = new BepStep(-1);
        assertFalse(bepStep.start());
    }

    @RepeatedTest(10)
    void validID(){
        BepStep bepStep = new BepStep(1);
        assertTrue(bepStep.start());
    }
}