package monitors.audisp;

import utils.Exec;
import utils.Log;

import java.io.File;
import java.net.URL;
import java.util.Arrays;

/**
 *  This class is used to handle the audit dispatcher and start multiplexing the kernel audit messages
 *  through a local port, so that raptor-client can then connect to it
 */
public class Audisp {

    private static final String NEW_AU_REMOTE_CONF_LOCATION = "audisp/au-remote.conf";
    private static final String NEW_AUDISP_REMOTE_CONF_LOCATION = "audisp/audisp-remote.conf";
    private static final String NEW_AUDITD_LOCATION = "audisp/auditd.conf";

    private static final String AU_REMOTE_CONF = "/etc/audisp/plugins.d/au-remote.conf";
    private static final String AUDISP_CONF = "/etc/audisp/audisp-remote.conf";
    private static final String AUDITD_CONF = "/etc/audit/auditd.conf";

    private static final String[] EXECVE64 = {"sudo", "auditctl", "-a", "always,exit", "-F", "arch=b64", "-S", "execve"};
    private static final String[] EXECVE32 = {"sudo", "auditctl", "-a", "always,exit", "-F", "arch=b32", "-S", "execve"};
    private static final String[] DEL_RULES = {"sudo", "auditctl", "-D"};
    private static final String[] SHOW_RULES = {"sudo", "auditctl", "-l"};

    private static final String[] PS = {"ps", "-A"};

    private static final String[] START_AUDITD = {"sudo", "systemctl", "start", "auditd"};
    private static final String[] STOP_AUDITD = {"sudo", "systemctl", "stop", "auditd"};
    private static final String[] DISABLE_AUDITD = {"sudo", "auditd", "-s", "disable"};
    private static final String[] ENABLE_AUDITD = {"sudo", "auditd", "-s", "enable"};
    private static final String[] STATUS_AUDITD = {"sudo", "systemctl", "status", "auditd"};

    private static final String AUDIT_SRC_LOC = utils.SystemOps.getCWD() + "/resources/audit-userspace/";

    public boolean startAudisp(){
        Log.info("Starting configurations for the auditd remote multiplexing.");
        if (!findRequiredExecutables()){
            compileAudit();
        }
        stopExistingAuditd();
        if (!updateConfigFiles()){
            Log.error("Failed to start audisp, could not swap the configuration files.");
            return false;
        }
        if (!startAuditd()){
            Log.error("Failed to start auditd");
            return false;
        }
        addRules();
        return checkIfRunning();
    }

    public boolean stopAuditd(){
        execute(STOP_AUDITD);
        return checkIfRunning();
    }

    /**
     * Makes sure that we have audisp-remote and audispd to make sure that we can multiplex.
     */
    private boolean findRequiredExecutables(){
        String[] audispLocations = {"/usr/bin/audisp", "/bin/audisp", "/sbin/audisp"};

        boolean audispFound = false;
        boolean audispRemoteFound = false;

        for(String location : audispLocations){
            if(utils.FileOperations.exists(location)){
                audispFound = true;
            }
            if(utils.FileOperations.exists(location + "-remote")){
                audispRemoteFound = true;
            }
        }
        Log.debug("Audisp-Remote: " + audispRemoteFound);
        Log.debug("Audisp: " + audispFound);
        return (audispRemoteFound && audispFound);
    }

    private void compileAudit(){
        Log.info("Failed to find all necessary files for auditd. Recompiling code Auditd.");
        String[] autogenCmd = {AUDIT_SRC_LOC + "autogen.sh"};
        String[] configure = {AUDIT_SRC_LOC + "configure"};
        String[] make = {"make", "-C", AUDIT_SRC_LOC, "-j", String.valueOf(utils.SystemOps.getCPUCores())};
        String[] makeInstall = {"make", "install" , "-C", AUDIT_SRC_LOC};

        Log.info("Generating audit-userspace config");
        executeAndWait(autogenCmd);
        Log.info("Configuring auditd-userspace");
        executeAndWait(configure);
        Log.info("Compiling auditd-userspace");
        executeAndWait(make);
        Log.info("Install auditd-userspace");
        executeAndWait(makeInstall);
    }

    private boolean stopExistingAuditd(){
        Exec stop = new Exec(STOP_AUDITD);
        stop.execute();
        stop.waitForProcessToDie();
        if(stop.getPid() == 0){
            Log.debug("Success: " + Arrays.toString(STOP_AUDITD));
            return true;
        }
        Log.error("Failed: " + Arrays.toString(STOP_AUDITD));
        Log.error("returned with exit code: " + stop.getPid());
        return false;
    }

    private boolean updateConfigFiles(){
        return (swapConfigFiles(NEW_AU_REMOTE_CONF_LOCATION, AU_REMOTE_CONF)
                && swapConfigFiles(NEW_AUDISP_REMOTE_CONF_LOCATION, AUDISP_CONF)
                && swapConfigFiles(NEW_AUDITD_LOCATION, AUDITD_CONF));
    }

    private boolean swapConfigFiles(String from, String to){
        ClassLoader classLoader = getClass().getClassLoader();

        URL resource = classLoader.getResource(from);
        if (resource == null) {
            Log.error("Failed to fetch resource " + from);
            return false;
        }
        File config = new File(resource.getFile());
        return config.renameTo(new File(to));
    }

    private boolean startAuditd(){
        Exec startAudit = new Exec(START_AUDITD);
        startAudit.execute();

        Exec enableAudit = new Exec(ENABLE_AUDITD);
        enableAudit.execute();

        Exec statusAudit = new Exec(STATUS_AUDITD);
        statusAudit.execute();

        return checkIfRunning();
    }

    private boolean checkIfRunning(){
        Exec ps = new Exec(PS);
        ps.disableShowOutput();
        ps.execute();
        ps.getOutput();
        String output = ps.getStdout();
        output = output.toLowerCase();
        return (output.contains("auditd") && output.contains("audisp"));
    }

    private void addRules(){
        execute(DEL_RULES);
        execute(EXECVE32);
        execute(EXECVE64);
    }

    private void execute(String[] command){
        Exec cmd = new Exec(command);
        cmd.execute();
    }

    private void executeAndWait(String[] command){
        Exec cmd = new Exec(command);
        Log.debug("Executing " + Arrays.toString(command));
        cmd.execute();
        cmd.getOutput();
        Log.debug(cmd.getStdout());
        Log.debug(cmd.getStderr());
    }
}
