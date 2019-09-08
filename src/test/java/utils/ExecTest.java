package utils;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class ExecTest {

    @Test
    void executeCommandGetOutputRealCommand() {
        String[] cmd = {"echo", "testing"};
        ArrayList<String> output = utils.Exec.executeCommandGetOutput(cmd);
        assertEquals("testing", output.get(0));
    }

    @Test
    void executeCommandGetOutputFakeCommand() {
        String[] cmd = {"echote", "testing"};
        ArrayList<String> output = utils.Exec.executeCommandGetOutput(cmd);
        assertTrue(output.isEmpty());
    }
}