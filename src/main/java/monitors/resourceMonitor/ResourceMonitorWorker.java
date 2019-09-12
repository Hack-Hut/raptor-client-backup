package monitors.resourceMonitor;

import utils.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * This class is used to populate the system utilization of
 * CPU and RAM, during the build. This information, in realtime,
 * is sent to the Raptor-Webserver. The web-server will then render
 * a graph to display to the users so that they users can check to
 * see if the build is freezing etc.
 *
 * The CPU monitor is another unique thread, that is queried by this thread.
 */
public class ResourceMonitorWorker implements Runnable{

    private boolean isRunning = true;
    private int memoryUsageMb = 0;
    private int memoryFreeMb = 0;
    private int memoryTotalMb = 0;
    private int elapsedTime = 0;
    private double cpuUsagePercentage = 0;
    private CpuUsage cpuMonitor;
    private MemoryUsageInterface memoryMonitor;
    private String logLocation;

    public ResourceMonitorWorker(String logLocation){
        this.logLocation = logLocation;
    }

    public void stop(){
        isRunning = false;
        cpuMonitor.finish();
    }

    private void getCpuMonitor(){
        cpuMonitor = new CpuUsage();
    }

    private void getMemoryMonitor(){
        if(utils.SystemOps.getOsType().contains("linux")){
            memoryMonitor = new MemoryUsageLinux();
        }
        else {
            memoryMonitor = new MemoryUsageWindows();
        }
    }

    private void getMemoryUsageMb(){
        memoryUsageMb = memoryMonitor.getUsedMemory();
        memoryFreeMb = memoryMonitor.getFreeMemory();
        memoryTotalMb = memoryMonitor.getTotalMemory();
    }

    private void getCpuUsage(){
        cpuUsagePercentage = cpuMonitor.getCPUUsagePercentage();
    }

    private void startCpuMonitor(){
        Thread cpuWorker = new Thread(cpuMonitor);
        cpuWorker.start();
    }

    private void sendUsage() throws IOException {
        writeUsageToLog();
        sendUsageToWebServer();
    }

    /**
     * Write a line in the following format to the log file
     * Time, MemoryUsageMB, MemoryFreeMB, MemoryTotalMB, PercentageCPU
     */
    private void writeUsageToLog() throws IOException {
        String lineToWrite = String.format("%s,%s,%s,%s,%s\n",
                elapsedTime, memoryUsageMb, memoryFreeMb, memoryTotalMb, cpuUsagePercentage);

        File logFile = new File(logLocation);
        try(FileWriter fr = new FileWriter(logFile, true)){
            fr.write(lineToWrite);
        }
    }

    private void sendUsageToWebServer(){
        //TODO send real time data usage to the raptor web-server
    }

    /**
     * Sleep for a minute, after every second check to see the current thread
     * should have been stopped.
     */
    private void sleepForAMinute(){
        for(int i = 0; i < 60; i+=1){
            if(!isRunning){
                return;
            }
            try {
                Thread.sleep( 1000);
            } catch (InterruptedException e) {
                Log.error(e.toString());
                Thread.currentThread().interrupt();
            }
        }
        elapsedTime ++;
    }

    public void run(){
        utils.misc.setThreadName(Thread.currentThread(), this.getClass());
        getCpuMonitor();
        getMemoryMonitor();
        startCpuMonitor();
        while(isRunning){
            try {
                getCpuUsage();
                getMemoryUsageMb();
                sendUsage();
            } catch (IOException e) {
                Log.error("Failed to send system usage information.");
                break;
            }
            sleepForAMinute();
        }

    }
}
