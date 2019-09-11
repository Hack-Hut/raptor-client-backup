package monitors;

import monitors.bepStep.ProcessMonitor;

public class BepStep implements MonitorInterface{

    private long pid;

    public  BepStep(long pid){
        this.pid = pid;
    }

    public boolean setup(){
        return true;
    }

    public boolean start(){
        ProcessMonitor processMonitor = new ProcessMonitor(this.pid);
        Thread processMonitorThread = new Thread(processMonitor);
        processMonitorThread.start();
        return true;
    }

    public boolean stop(){
        return true;
    }
}
