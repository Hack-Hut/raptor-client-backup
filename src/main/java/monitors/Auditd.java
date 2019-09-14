package monitors;

import utils.Log;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import static utils.Exec.executeCommandGetOutput;

/**
 * This is the less efficient but more reliable method for getting auditing information from
 * a Linux build system. Rather than using the audisp-remote plugin this just uses vanilla
 * auditd to log syscalls and (possible read writes) to the auditd.log file. This class
 * should be used as a fall back option to audisip
 */
public class Auditd implements MonitorInterface, AuditInterface {
    private static final String NEW_AU_REMOTE_CONF_LOCATION = "/auditd/au-remote.conf";
    private static final String NEW_AUDISP_REMOTE_CONF_LOCATION = "/auditd/audisp-remote.conf";
    private static final String NEW_AUDITD_LOCATION = "/auditd/auditd.conf";

    private static final String AU_REMOTE_CONF = "/etc/audisp/plugins.d/au-remote.conf";
    private static final String AUDISP_CONF = "/etc/audisp/audisp-remote.conf";
    private static final String AUDITD_CONF = "/etc/audit/auditd.conf";

    private static final String[] EXECVE64 = {"sudo", "auditctl", "-a", "always,exit", "-F", "arch=b64", "-S", "execve"};
    private static final String[] EXECVE32 = {"sudo", "auditctl", "-a", "always,exit", "-F", "arch=b32", "-S", "execve"};
    private static final String[] DEL_RULES = {"sudo", "auditctl", "-D"};
    private static final String[] SHOW_RULES = {"sudo", "auditctl", "-l"};
    private static final String[] PS= {"ps", "-A"};

    private static final String[] START_AUDITD = {"sudo", "systemctl", "start", "auditd"};
    private static final String[] STOP_AUDITD = {"sudo", "systemctl", "stop", "auditd"};
    private static final String[] DISABLE_AUDITD = {"sudo", "auditd", "-s", "disable"};
    private static final String[] ENABLE_AUDITD = {"sudo", "auditd", "-s", "enable"};
    private static final String[] STATUS_AUDITD = {"sudo", "systemctl", "status", "auditd"};

    private static final String[] REAP_AUDISP = {"sudo", "killall", "audispd"};
    private static final String[] REAP_AUDITD = {"sudo", "killall", "auditd"};
    private static final String[] REAP_AUDISP_REMOTE = {"sudo", "killall", "audsip-remote"};

    private static final String AUDITD_LOG_LOCATION = "/usr1/auditd.log";

    private buildToolsConfigGenerator.auditors.Auditd auditParser; // TODO: refactor this, the name is stupid!

    public boolean setup(){
        stop();
        if (!clearOldLog()){
            Log.error("Failed to delete the previous log file at " + AUDITD_LOG_LOCATION + ".");
            return false;
        }
        updateConfigFiles();
        chownConfig();
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
        sleepThread();
        if (!isAuditdRunning()){
            executeCommandGetOutput(START_AUDITD);
            sleepThread();
            sleepThread();
            if(!isAuditdRunning()){
                Log.error("Failed to start monitors.auditd with " + Arrays.toString(START_AUDITD));
                return false;
            }
            return true;
        }
        Log.error("Failed to kill the previously running version of auditd.");
        stop();
        return start();
    }

    /**
     * Simulates build commands and makes sure that the commands
     * that were executed end up in the auditd log.
     * @return if the test passed
     */
    public boolean test(){
        // Allow auditd startup to finish
        Log.info("Testing to see if auditd has started correctly.");
        sleepThread();
        executeTestCommands();


        boolean uname = false;
        boolean whoami = false;
        boolean ls = false;
        ArrayList<String> executablesFound;
        try {
            executablesFound = findTestExecutables();
        } catch (IOException e) {
            return false;
        }
        for(String executable : executablesFound){
            if (executable.contains("uname")) uname = true;
            if (executable.contains("whoami")) whoami = true;
            if (executable.contains("ls")) ls = true;
        }
        boolean res = (uname && whoami && ls);
        if(!res){
            Log.error("Expected to see \"uname\", \"whoami\" and \"ls\"");
            Log.error(executablesFound.toString());
        }
        return res;
    }

    /**
     * Stop the auditd deamon and all of it child processes.
     * @return is the processess is running
     */
    public boolean stop(){
        //Auditd is pretty stubborn on one of the test environments.
        executeCommandGetOutput(STOP_AUDITD);
        executeCommandGetOutput(REAP_AUDISP);
        executeCommandGetOutput(REAP_AUDISP_REMOTE);
        executeCommandGetOutput(REAP_AUDITD);
        executeCommandGetOutput(STOP_AUDITD);
        executeCommandGetOutput(REAP_AUDISP);
        executeCommandGetOutput(REAP_AUDISP_REMOTE);
        executeCommandGetOutput(REAP_AUDITD);
        return isAuditdRunning();
    }

    public Object[] getExecutables(){
        return new HashSet<Object>().toArray();
    }

    @Override
    public boolean generateConfigurationFiles() {
        getAuditdParser();
        return auditParser.generateConfigurationFiles();
    }

    private boolean clearOldLog(){
        if(isAuditdRunning()){
            Log.error("Cannot delete old log file at " + AUDITD_LOG_LOCATION + " while auditd is running.");
            return false;
        }
        File auditdLogLocation = new File(AUDITD_LOG_LOCATION);
        auditdLogLocation.delete();
        String[] cmd = {"sudo", "rm", "-rf", AUDITD_LOG_LOCATION};
        utils.Exec.executeCommandGetOutput(cmd);
        return (!auditdLogLocation.exists());
    }

    private void getAuditdParser(){
        auditParser = new buildToolsConfigGenerator.auditors.Auditd(AUDITD_LOG_LOCATION);
    }

    private boolean isAuditdRunning(){
        for (String line : executeCommandGetOutput(PS)) {
            if (line.contains("auditd") && (!line.contains("kauditd"))){
                Log.error(line);
                return true;
            }
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

    private void executeTestCommands(){
        Runtime r = Runtime.getRuntime();
        try {
            Process p = r.exec("uname");
            Process p2 = r.exec("whoami");
            Process p3 = r.exec("ls");
            p.waitFor();
            p2.waitFor();
            p3.waitFor();

        } catch (InterruptedException | IOException e) {
            Log.error("Failed to execute audisp test commands");
            Log.error(e.toString());
            Log.error(Arrays.toString(e.getStackTrace()));
        }
    }

    private void sleepThread(){
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            Log.error("Audisp worker thread was interrupted.");
        }
    }

    private ArrayList<String> findTestExecutables() throws IOException {
        ArrayList<String>  executablesFound = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(AUDITD_LOG_LOCATION))){
            String line = reader.readLine();
            while (line != null) {
                if (line.contains("exe=")) executablesFound.add(line);
                line = reader.readLine();
            }
            return executablesFound;
        } catch (IOException e) {
            Log.error("Raptor-Client failed to read " + AUDITD_LOG_LOCATION);
            Log.error("Are you root?");
            Log.error("If you have to run the build as a lower privileged user then try the following.");
            Log.error("Use visudo to allow the current user to run JRE as root.");
            throw new IOException("Failed to read " + AUDITD_LOG_LOCATION);
        }
    }

    private boolean updateConfigFiles(){
        return (swapConfigFiles(NEW_AU_REMOTE_CONF_LOCATION, AU_REMOTE_CONF)
                && swapConfigFiles(NEW_AUDISP_REMOTE_CONF_LOCATION, AUDISP_CONF)
                && swapConfigFiles(NEW_AUDITD_LOCATION, AUDITD_CONF));
    }

    private void chownConfig(){
        String[] auRemote = {"sudo", "chown", "root", AU_REMOTE_CONF};
        String[] auditd = {"sudo", "chown", "root", AUDISP_CONF};
        String[] audisp = {"sudo", "chown", "root", AUDITD_CONF};
        utils.Exec.executeCommandGetOutput(auRemote);
        utils.Exec.executeCommandGetOutput(auditd);
        utils.Exec.executeCommandGetOutput(audisp);
    }

    private boolean swapConfigFiles(String from, String to){
        URL fileUrl = getClass().getResource(from);
        try(
                FileOutputStream out = new FileOutputStream(to);
                BufferedInputStream in = new BufferedInputStream(fileUrl.openStream()))
        {
            byte[] dataBuffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                out.write(dataBuffer, 0, bytesRead);
            }
            out.close();
            Log.debug("Inserting JAR configuration file " + from + " to " + to);
            return true;
        } catch (IOException e) {
            Log.error("Failed writing " + from + " to " + to);
            Log.error(e.toString());
        }
        return false;
    }

    public static void main(String[] args){
        Auditd auditd = new Auditd();
        auditd.stop();
        System.out.println(auditd.isAuditdRunning());
        System.out.println(auditd.clearOldLog());
    }


}
