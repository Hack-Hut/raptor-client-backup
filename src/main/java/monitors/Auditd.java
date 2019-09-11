package monitors;

import utils.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static utils.Exec.executeCommandGetOutput;

public class Auditd implements MonitorInterface, AuditInterface {

    private static final String[] EXECVE64 = {"sudo", "auditctl", "-a", "always,exit", "-F", "arch=b64", "-S", "execve"};
    private static final String[] EXECVE32 = {"sudo", "auditctl", "-a", "always,exit", "-F", "arch=b32", "-S", "execve"};
    private static final String[] DEL_RULES = {"sudo", "auditctl", "-D"};
    private static final String[] SHOW_RULES = {"sudo", "auditctl", "-l"};
    private static final String[] PS= {"ps", "-A"};

    private static final String[] START_AUDITD = {"sudo", "systemctl", "start", "auditd"};
    private static final String[] DISABLE_AUDITD = {"sudo", "auditd", "-s", "disable"};
    private static final String[] ENABLE_AUDITD = {"sudo", "auditd", "-s", "enable"};
    private static final String[] STATUS_AUDITD = {"sudo", "systemctl", "status", "auditd"};


    public boolean setup(){
        Log.debug("Starting monitors.auditd");
        if (!start()){
            return false;
        }

        Log.debug("Deleting previous auditctl rules");
        if (!deleteRules()) {
            Log.error("Failed to delete the monitors.auditd rules with command: " + Arrays.toString(DEL_RULES));
            return false;
        }

        Log.debug("Adding the following monitors.auditd rules");
        Log.debug(Arrays.toString(EXECVE32));
        Log.debug(Arrays.toString(EXECVE64));
        executeCommandGetOutput(EXECVE32);
        executeCommandGetOutput(EXECVE64);

        Log.debug("Auditd about to start with the following rules");
        Log.debug(getAuditdRules().toString());

        Log.debug("Enabling monitors.auditd");
        executeCommandGetOutput(ENABLE_AUDITD);

        Log.debug("sudo service auditd status returned: ");
        for (String line : executeCommandGetOutput(STATUS_AUDITD)) Log.info(line);
        return true;
    }

    public boolean start(){
        if (isAuditdRunning()){
            executeCommandGetOutput(START_AUDITD);
            if(!isAuditdRunning()){
                Log.error("Failed to start monitors.auditd with " + Arrays.toString(START_AUDITD));
                return false;
            }
        }
        return true;
    }

    public boolean stop(){
        executeCommandGetOutput(DISABLE_AUDITD);
        return true;
    }

    public Set<String> getExecutables(){
        //TODO getExecutables auditd
        return new HashSet<>();
    }

    private boolean isAuditdRunning(){
        for (String line : executeCommandGetOutput(PS)) {
            if (line.contains("monitors/auditd")) return true;
        }
        return false;
    }

    private ArrayList<String> getAuditdRules(){
        return executeCommandGetOutput(SHOW_RULES);
    }

    private boolean deleteRules(){
        executeCommandGetOutput(DEL_RULES);
        ArrayList<String> rules = executeCommandGetOutput(SHOW_RULES);
        String first;
        if(rules.size() == 1) first = rules.get(0);
        else return false;
        return first.contains("No rules");
    }
}
