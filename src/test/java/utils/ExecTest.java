package utils;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExecTest {
    private String os = utils.SystemOps.getOsType().toLowerCase();

    @Test
    void executeCommandGetOutputRealCommand() {
        if(os.equals("linux")) {
            String[] cmd = {"echo", "testing"};
            ArrayList<String> output = utils.Exec.executeCommandGetOutput(cmd);
            assertEquals("testing", output.get(0));
        }
        else{
            String[] cmd = {"cmd.exe", "/c", "echo", "testing"};
            ArrayList<String> output = utils.Exec.executeCommandGetOutput(cmd);
            assertEquals("testing", output.get(0));
        }

    }

    @Test
    void executeCommandGetOutputFakeCommand() {
        String[] cmd = {"echote", "testing"};
        ArrayList<String> output = utils.Exec.executeCommandGetOutput(cmd);
        assertTrue(output.isEmpty());
    }
}