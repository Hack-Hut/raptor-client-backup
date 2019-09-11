package raptorClient.master;

import monitors.*;
import monitors.bepStep.ProcessMonitor;
import monitors.resourceMonitor.ResourceMonitor;
import utils.Exec;
import utils.Log;

import java.util.ArrayList;
import java.util.List;

public class Initial extends raptorClient.master.MasterController{

    private NullCatcher nullCatcher;
    private Auditd audit;
    private Audisp audisp;
    private AuditInterface auditor;
    private ResourceMonitor resourceMonitor;
    private BepStep bepStep;

    public Initial(int buildId, String stage, String[] buildCommand){
        super(buildId, stage, buildCommand);
    }

    public void getMonitors(){
        getResourceMonitorInstance();
        getNullCatcherInstance();
        getAudispInstance();
        getAuditInstance();
    }

    @Override
    public boolean startMonitors(){
        startAMonitor(resourceMonitor);
        if (os.contains("linux")){
            startAMonitor(nullCatcher);

            if (!startAMonitor(audisp)){
                Log.error("Audit dispatcher failed to multiplex, falling back to slower method.");

                if(!startAMonitor(audit)){
                    Log.error("Raptor failed to interact successfully with Auditd.");
                    Log.error("Auditd is required for initial Linux build.");
                    System.exit(-1);
                }
                auditor = audit;
            }
            else{
                auditor = audisp;
            }
        }
        else{
            // TODO windows start monitors.
        }
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
            startBepStep();
            process.getOutput();
            sleepMainThread(10);
            returnCode = process.getProcess().exitValue();
            return true;
        }
        Log.error("Execute Build command returned a process ID of 0");
        Log.error("This would only occur if CPU usage was at 100% before executing the build command.");
        Log.error("This is because the main thread (even after sleeping) got scheduled before the execution thread.");
        return false;
    }

    public boolean stopMonitor(){
        nullCatcher.stop();
        auditor.stop();
        resourceMonitor.stop();
        bepStep.stop();
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
    
    private void getResourceMonitorInstance(){
        resourceMonitor = new ResourceMonitor(buildId, 0, os);
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

    private boolean startAMonitor(MonitorInterface monitor){
        if(monitor.setup()){
            monitor.start();
            return true;
        }
        Log.error(monitor.getClass().getSimpleName() + " failed to setup");
        return false;
    }

    public boolean processResults() {
        return  true;
    }
    public boolean connectToSlaves() {
        return  true;
    }

    private void startAuditd(){
        audit.start();
    }

    private boolean startAudisp(){
        return audisp.start();
    }

    private void startBepStep(){
        if (os.equals("Linux")) {
            ProcessMonitor processMonitor = new ProcessMonitor(pid);
            Thread bepStepThread = new Thread(processMonitor);
            bepStepThread.start();
        }
    }

}
