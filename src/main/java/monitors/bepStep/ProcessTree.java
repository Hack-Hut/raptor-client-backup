package monitors.bepStep;

import utils.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ProcessTree implements Runnable{
    private static String cwd = new File("").getAbsolutePath();
    private static final String logLocation = cwd + "/logs/bep-step.log";
    private Integer pid;
    private FileWriter fw;
    private Integer buildScriptCount = 0;
    private boolean test = false;
    private ArrayList<String> commands = new ArrayList<>();

    public ProcessTree(long pid){
        this.pid = (int) pid;
        this.fw = getFwAndClearFile();
    }

    public void setTestMode(){
        // Changes a few things to make testing abit easier.
        test = true;
        try {
            fw = new FileWriter("/dev/null");
        } catch (IOException e) {
            Log.error(e.toString());
        }
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

    private void createTree(){
        Set<Integer> allRunningBuildProcesses = new HashSet<>();
        allRunningBuildProcesses.add(pid);
        Log.info("Attaching to PID: " + pid);
        monitors.bepStep.linux.Process rootProcess = new monitors.bepStep.linux.Process(pid);
        while(rootProcess.isRunning()){
            Set<Integer> potentialNewProcs = new HashSet<>();
            for (Integer proc: allRunningBuildProcesses){
                potentialNewProcs.addAll(monitors.bepStep.linux.Process.getChildren(proc));
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
            monitors.bepStep.linux.Process rootProcess = new monitors.bepStep.linux.Process(proc);
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

    public ArrayList<String> getCommands(){
        return this.commands;
    }

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
