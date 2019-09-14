package stage.master;

import monitors.*;
import utils.Exec;
import utils.Log;

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
public class Initial extends stage.master.MasterController{

    private NullCatcher nullCatcher;
    private AuditInterface auditor;
    private BepStep bepStep;
    private Audisp audisp;
    private Auditd audit;

    private boolean monitorRunning = false;

    public Initial(int buildId, String stage, String[] buildCommand){
        super(buildId, stage, buildCommand);
        getMonitors();
    }

    @Override
<<<<<<< HEAD:src/main/java/stage/master/Initial.java
    public boolean startMonitors(){
        this.startResourceMonitor();
        if (os.contains("Linux")){
            startAMonitor(nullCatcher, "dev-null-catcher");
            if (!startAuditor()) { // Starts either auditd, or auditd->audisp-remote
                return false;
            }
=======
    public boolean startMonitors() throws MonitorFailureException {
        monitorRunning = true;
        this.startResourceMonitor();
        if (os.contains("Linux")){
            startAMonitor(nullCatcher, "dev-null-catcher");
            return startAuditor();
>>>>>>> detachedHead:src/main/java/raptorClient/master/Initial.java
        }
        else{
            // TODO windows start monitors.
        }
        Log.info("Starting bep-step thread after build execution has started.");
<<<<<<< HEAD:src/main/java/stage/master/Initial.java

        monitorRunning = true;
=======
>>>>>>> detachedHead:src/main/java/raptorClient/master/Initial.java
        return true;
    }

    /**
     * Spawn a thread to execute the build command
     * Then spawn another thread to execute bep-step
     * main.java.entryPoint.Main thread then listens to the Process std streams
     */
    public boolean executeBuild() {
        Exec process = this.executeCommand();
        pid = process.getPid();
        setBepStepInstance(new BepStep(pid));
        if (pid != 0) {
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
            return false;
        }

        this.stopResourceMonitor();
        nullCatcher.stop();
        auditor.stop();
         if(bepStep == null) Log.error("Failed to stop bep-step because it never started");
        else bepStep.stop();

        monitorRunning = false;
        this.logRunningThreads();
        return true;
    }

    @Override
    public void processResults(){
        utils.misc.showLogFileList();
        auditor.generateConfigurationFiles();
    }

    public void uploadResults(){
    }

    private void getMonitors(){
        resourceMonitor = new ResourceMonitor();
        nullCatcher = new NullCatcher();
        audisp = new Audisp();
        audit = new Auditd();
<<<<<<< HEAD:src/main/java/stage/master/Initial.java
    }

    private boolean startAMonitor(MonitorInterface monitor, String monitorName){
=======
    }

    public void setResourceMonitorInstance(ResourceMonitor resmon){
        resourceMonitor = resmon;
    }

    public void setNullCatcherInstance(NullCatcher nullCatch){
        nullCatcher = nullCatch;
    }

    public void setAudispInstance(Audisp auditor){
        audisp = auditor;
    }

    public void setAuditInstance(Auditd auditor){
        audit = auditor;
    }

    private void getBepStepInstance(){
        bepStep = new BepStep(pid);
    }

    private boolean startAMonitor(MonitorInterface monitor, String monitorName) throws MonitorFailureException {
>>>>>>> detachedHead:src/main/java/raptorClient/master/Initial.java
        Log.info("Attempting to start " + monitorName);
        if(monitor.setup()){
            if(monitor.start()){
                Log.info(monitorName + " started.");
                Log.info("");
                if (monitor.test()){
                    Log.info(monitorName + " passed the tests.");
                    return true;
                }
                else{
                    Log.error(monitorName + " started but the running tests failed.");
                }
            }
            Log.error("Failed to start " + monitorName);
        }
        else {
            Log.error(monitorName + " setup failed!");
        }
        Log.info("");
        throw new MonitorFailureException("Failed to start " + monitorName);
    }

    private boolean startAuditor() throws MonitorFailureException {
        try{
            startAMonitor(audisp, "audisp-remote");
            auditor = audisp;
        }catch (MonitorFailureException e){
            Log.error("Audit dispatcher failed to multiplex, falling back to slower method.");
            if(!startAMonitor(audit, "auditd")){
                Log.error("Raptor failed to interact successfully with Auditd or Audisp-remote.");
                throw new MonitorFailureException("Raptor failed to interact successfully with Auditd or Audisp-remote.");
            }
            auditor = audit;
        }
        return true;
    }

    public void setResourceMonitorInstance(ResourceMonitor monitor){
        resourceMonitor = monitor;
    }

    public void setNullCatcherInstance(NullCatcher nullCatch){
        nullCatcher = nullCatch;
    }

    public void setAudispInstance(Audisp audit){
        audisp = audit;
    }

    public void setAuditInstance(Auditd auditd){
        audit = auditd;
    }

    public void setBepStepInstance(BepStep bs){
        bepStep = new BepStep(pid);
    }
}
