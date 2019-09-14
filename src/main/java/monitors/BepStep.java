package monitors;

import monitors.bepStep.ProcessMonitor;
import utils.Log;

public class BepStep implements MonitorInterface{

    private long pid;

    public  BepStep(long pid){
        this.pid = pid;
    }

    public BepStep() {

    }

    public boolean setup(){
        return true;
    }

    public boolean start(){
        if (this.pid  == -1){
            Log.error("Invalid process ID.");
            return false;
        }
        ProcessMonitor processMonitor = new ProcessMonitor(this.pid);
        Thread processMonitorThread = new Thread(processMonitor);
        processMonitorThread.start();

        try{
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Log.error(e.toString());
            Thread.currentThread().interrupt();
        }

        return processMonitor.rootProcessIsAlive();
    }

    public boolean test(){
        return true;
    }

    public boolean stop(){
        return true;
    }
}
