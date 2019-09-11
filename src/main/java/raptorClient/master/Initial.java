package raptorClient.master;

import monitors.*;
import utils.Exec;
import utils.Log;

import java.util.ArrayList;
import java.util.List;

public class Initial extends raptorClient.master.MasterController{

    private NullCatcher nullCatcher;
    private AuditInterface auditor;
    private BepStep bepStep;
    private Audisp audisp;
    private Auditd audit;

    private boolean monitorRunning = false;

    Initial(int buildId, String stage, String[] buildCommand){
        super(buildId, stage, buildCommand);
    }

    @Override
    public boolean startMonitors(){
        getMonitors();
        this.startResourceMonitor();
        if (os.contains("Linux")){
            startAMonitor(nullCatcher, "dev-null-catcher");
            if (!startAuditor()) { // Starts either auditd, or auditd->audisp-remote
                return false;
            }
        }
        else{
            // TODO windows start monitors.
        }

        Log.info("Starting bep-step thread after build execution has started.");

        monitorRunning = true;
        return true;
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
            getBepStepInstance();
            bepStep.start();
            sleepMainThread(10);
            System.out.println("\n");
            process.getOutput();
            System.out.println("\n");
            sleepMainThread(10);
            returnCode = process.getProcess().exitValue();
            return true;
        }
        Log.error("Execute Build command returned a process ID of 0");
        Log.error("This would only occur if CPU usage was at 100% before executing the build command.");
        Log.error("This is because the main thread (even after sleeping) got scheduled before the execution thread.");
        return false;
    }

    public boolean stopMonitors(){
        if(!monitorRunning){
            Log.error("Logic error, stopMonitors was called even though startMonitors was never called.");
        }

        this.stopResourceMonitor();
        nullCatcher.stop();
        auditor.stop();
        bepStep.stop();

        monitorRunning = false;
        this.logRunningThreads();
        return true;
    }

    @Override
    public boolean processResults(){
        utils.misc.showLogFileList();
        auditor.generateConfigurationFiles();
        return true;
    }

    public List<String> findSlaves(){
        return new ArrayList<>();
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

    private void getMonitors(){
        getResourceMonitorInstance();
        getNullCatcherInstance();
        getAudispInstance();
        getAuditInstance();
    }

    private void getResourceMonitorInstance(){
        resourceMonitor = new ResourceMonitor();
    }

    private void getNullCatcherInstance(){
        nullCatcher = new NullCatcher();
    }

    private void getAudispInstance(){
        audisp = new Audisp();
    }

    private void getAuditInstance(){
        audit = new Auditd();
    }

    private void getBepStepInstance(){
        bepStep = new BepStep(pid);
    }

    private boolean startAMonitor(MonitorInterface monitor, String monitorName){
        Log.info("Attempting to start " + monitorName);
        if(monitor.setup()){
            boolean started = monitor.start();
            Log.info(monitorName + " started.");
            Log.info("");
            return started;
        }
        Log.error(monitorName + " setup failed!");
        Log.info("");
        return false;
    }

    private boolean startAuditor(){
        if (!startAMonitor(audisp, "audisp-remote")){
            Log.error("Audit dispatcher failed to multiplex, falling back to slower method.");

            if(!startAMonitor(audit, "auditd")){
                Log.error("Raptor failed to interact successfully with Auditd or Audisp-remote.");
                System.exit(-1);
                return false;
            }
            auditor = audit;
        }
        else{
            auditor = audisp;
        }
        return true;
    }
    public boolean connectToSlaves() {
        return  true;
    }

}
