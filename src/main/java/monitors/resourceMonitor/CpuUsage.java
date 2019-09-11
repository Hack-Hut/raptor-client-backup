package monitors.resourceMonitor;

import com.sun.management.OperatingSystemMXBean;
import utils.Log;

import java.lang.management.ManagementFactory;
import java.util.concurrent.TimeUnit;

public class CpuUsage implements Runnable{
    private volatile boolean exit = false;
    private double currentCPUUsage = 0;

    public void finish(){
        Log.debug("Stopping the CPU System monitor thread.");
        exit = true;
    }

    public double getCPUUsagePercentage(){
        return currentCPUUsage;
    }

    private void monitorCPU(){
        OperatingSystemMXBean operatingSystemMXBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        while(!exit) {
            currentCPUUsage = operatingSystemMXBean.getSystemCpuLoad();
            sleep();
        }
    }

    private void sleep(){
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException ignore) {
            Thread.currentThread().interrupt();
        }
    }


    public void run(){
        Log.debug("Starting the CPU System monitor thread.");
        monitorCPU();
    }
}
