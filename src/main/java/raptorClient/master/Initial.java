package raptorClient.master;

import monitors.*;
import utils.Exec;
import utils.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * This class handles the initial build stage for BEP. The initial build stage potentially
 * has the most setup. Rather than just following the build document and then running the build.
 * The BEP team is also encouraged to collect things sent to /dev/null, generate a binary hash list,
 * monitor system resources during the build, find undisclosed log files and collect build logs.
 * Then what seems to be hardest part is to neatly organize these logs, deleting partial logs,
 * compress them, process them and upload them to a central repository with a good enough name
 * that describes to others what the logs relate to.
 *
 * This is a highly error prone task when done manually especially when the build has multi build
 * environments and the analyst is doing multiple builds at any given time. 
 */
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
    public void startMonitors(){
        getMonitors();
        this.startResourceMonitor();
        if (os.contains("Linux")){
            startAMonitor(nullCatcher, "dev-null-catcher");
            if (!startAuditor()) { // Starts either auditd, or auditd->audisp-remote
                return;
            }
        }
        else{
            // TODO windows start monitors.
        }

        Log.info("Starting bep-step thread after build execution has started.");

        monitorRunning = true;
    }

    /**
     * Spawn a thread to execute the build command
     * Then spawn another thread to execute bep-step
     * main.java.entryPoint.Main thread then listens to the Process std streams
     */
    public void executeBuild() {
        Exec process = this.executeCommand();
        pid = process.getPid();
        getBepStepInstance();
        if (pid != 0) {
            bepStep.start();
            sleepMainThread(10);
            System.out.println("\n");
            process.getOutput();
            System.out.println("\n");
            sleepMainThread(10);
            returnCode = process.getProcess().exitValue();
            return;
        }
        Log.error("Execute Build command returned a process ID of 0");
        Log.error("This would only occur if CPU usage was at 100% before executing the build command.");
        Log.error("This is because the main thread (even after sleeping) got scheduled before the execution thread.");
    }

    public void stopMonitors(){
        if(!monitorRunning){
            Log.error("Logic error, stopMonitors was called even though startMonitors was never called.");
        }

        this.stopResourceMonitor();
        nullCatcher.stop();
        auditor.stop();
        bepStep.stop();

        monitorRunning = false;
        this.logRunningThreads();
    }

    @Override
    public void processResults(){
        utils.misc.showLogFileList();
        auditor.generateConfigurationFiles();
    }

    public List<String> findSlaves(){
        return new ArrayList<>();
    }

    public void cleanMachine(){
    }

    public void uploadResults(){
    }

    public void killSlaves(){
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
    public void connectToSlaves() {
    }

}
