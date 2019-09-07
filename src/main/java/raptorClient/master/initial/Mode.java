package raptorClient.master.initial;

import monitors.auditd.Auditd;
import monitors.bepStep.ProcessTree;
import monitors.nullCatcher.NullCatcher;
import utils.Exec;
import utils.Log;

public class Mode extends raptorClient.master.MasterController{

    private NullCatcher nullCatcher = new NullCatcher();
    private Auditd audit = new Auditd();

    public Mode(int buildId, String stage, String[] buildCommand){
        super(buildId, stage, buildCommand);
    }

    @Override
    public boolean startMonitor(){
        startSysResourceMonitor();
        if (this.os.toLowerCase().equals("linux")){
            startAuditd();
            if(nullCatcher.start()){
                Log.info("Null-Catcher started");
            }
            else{
                Log.error("Null-Catcher failed to start.");
            }
        }
        else{
            Log.error("TODO procmon");
        }
        return true;
    }

    public boolean stopMonitor(){
        nullCatcher.stop();
        return true;
    }
    public boolean findSlaves(){
        return true;
    }
    public boolean pingSlave(){
        return true;
    }
    public boolean cleanMachine(){
        return true;
    }
    public boolean uploadResults(){
        return true;
    }
    public boolean killSlaves(){
        return true;
    }
    public boolean processResults() {
        return  true;
    }

    /**
     * Spawn a thread to execute the build command
     * Then spawn another thread to execute bep-step
     * Then spawn another thread to monitor system resource utilization
     * main.java.entryPoint.Main thread then listens to the Process input/output streams
     * @return Success
     */
    public boolean executeBuild() {
        Exec process = this.executeCommand();
        pid = process.getPid();
        if (pid != 0) {
            startBepStep();
            process.getOutput();
            sleepMainThread(10);
            stopSysResourceMonitor();
            returnCode = process.getProcess().exitValue();
            return true;
        }
        Log.error("Execute Build command returned a process ID of 0");
        Log.error("This would only occur if CPU usage was at 100% before executing the build command.");
        Log.error("This is because the main thread (even after sleeping) got scheduled before the execution thread.");
        return false;
    }
    public boolean connectToSlaves() {
        return  true;
    }

    private void startAuditd(){
        audit.setupAuditing();
    }


    private void startBepStep(){
        if (os.equals("Linux")) {
            ProcessTree bepStep = new ProcessTree(pid);
            Thread bepStepThread = new Thread(bepStep);
            bepStepThread.start();
        }
    }

}
