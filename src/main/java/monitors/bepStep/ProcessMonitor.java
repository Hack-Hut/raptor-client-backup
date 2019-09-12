package monitors.bepStep;

import monitors.bepStep.linux.Process;
import utils.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * This class is used to attach to a process
 * The process is then monitored for newly created child processes
 * Any child process is then examined through the proc file system
 * to get environment variables, open file descriptors etc.
 * This information is then logged into bep-step.log. The sole idea of this
 * log file is to aid debugging, not to be used in any reports. This is
 * because of the following reason.
 *
 * Note
 * This method has the draw back of only collecting information about
 * a process at the exact point that it's monitored. So the information
 * can be assumed to be accurate at some point of the processes life,
 * but is not comprehensive.
 */
public class ProcessMonitor implements Runnable{
    private static String cwd = new File("").getAbsolutePath();
    private static final String logLocation = cwd + "/logs/bep-step.log";
    private Integer pid;
    private FileWriter fw;
    private Integer buildScriptCount = 0;
    private ArrayList<String> commands = new ArrayList<>();
    private Process rootProcess;

    public ProcessMonitor(long pid){
        this.pid = (int) pid;
        this.fw = getFwAndClearFile();
        rootProcess = new Process(pid);
    }

    public boolean rootProcessIsAlive(){
        return rootProcess.isRunning();
    }

    public ArrayList<String> getCommands(){
        return this.commands;
    }

    private FileWriter getFwAndClearFile(){
        File f = new File(logLocation);
        if(f.exists()) {
            //noinspection ResultOfMethodCallIgnored
            f.delete();
        }
        try {
            fw = new FileWriter(logLocation);
        } catch (IOException e) {
            Log.error("Failed to File writer, bep-step");
            Log.error(e.toString());
        }
        return fw;
    }

    /**
     * While the root process is alive, query the proc file system for child
     * processes, then log the information about the child process, then
     * remove all no longer running processes.
     */
    private void createTree(){
        Set<Integer> allRunningBuildProcesses = new HashSet<>();
        allRunningBuildProcesses.add(pid);
        Log.info("Attaching to PID: " + pid);
        while(rootProcessIsAlive()){
            Set<Integer> potentialNewProcs = new HashSet<>();
            for (Integer proc: allRunningBuildProcesses){
                potentialNewProcs.addAll(Process.getChildren(proc));
            }
            // now contains newly spawned processes
            potentialNewProcs.removeAll(allRunningBuildProcesses);
            writeNewProcToLog(potentialNewProcs);
            allRunningBuildProcesses = purgeDeadChildren(allRunningBuildProcesses);
            allRunningBuildProcesses.addAll(potentialNewProcs);

            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                Log.error(e.toString());
                Thread.currentThread().interrupt();
            }
        }
        Log.info("Build Script ran " + buildScriptCount + " different Shell/Python scripts.");
        try {
            fw.close();
        } catch (IOException | NullPointerException e) {
            Log.error("Error closing bep-step log file.");
        }
        Log.debug("Stopping the bep-step thread.");
    }

    private void writeNewProcToLog(Set<Integer> potentialNewProcs){
        for (Integer proc: potentialNewProcs) {
            Process rootProcess = new Process(proc);
            String cmd = rootProcess.getCmd();
            if (!(cmd.contains("python") | cmd.contains("sh"))) { // TODO Invert ! when out of testing phase
                buildScriptCount ++;
                String ppid = rootProcess.getPpid().toString();
                String fd = rootProcess.getDescriptors().toString();
                String environ = rootProcess.getEnviron();
                String cwd = rootProcess.getCwd();
                try {
                    fw.write("PID: " + proc);
                    fw.write("\nPPID: " + ppid);
                    fw.write("\nCommand: " + cmd);
                    fw.write("\nCurrent Working Dir: " + cwd);
                    fw.write("\nFile Descriptor: " + fd);
                    //fw.write("\nEnvironment " + environ);
                    fw.write("\n\n");
                    boolean test = false;
                    if(test){
                       commands.add(cmd);
                    }
                } catch (IOException e) {
                    Log.error("Error writing to bep-step.");
                    Log.error(e.toString());
                }
            }
        }
    }

    /**
     * For a list of processes this method will remove the processes that
     * are no longer running
     * @param allProcesses: List of processes.
     */
    private Set<Integer> purgeDeadChildren(Set<Integer> allProcesses){
        Set<Integer> deadProcesses = new HashSet<>();
        for (Integer proc: allProcesses){
            File procFile = new File("/proc/" + proc);
            if (!procFile.exists()) deadProcesses.add(proc);
        }
        allProcesses.removeAll(deadProcesses);
        return allProcesses;
    }

    public void run(){
        Thread.currentThread().setName("Bep-Step");
        Log.info("Starting the bep-step thread.");
        createTree();
    }
}
