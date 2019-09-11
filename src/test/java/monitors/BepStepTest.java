package monitors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BepStepTest {

    @BeforeEach
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