package monitors.resourceMonitor;

import monitors.MonitorInterface;
import utils.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ResourceMonitor implements Runnable, MonitorInterface {
    private static final String CWD = new File("").getAbsolutePath();
    private static final String LOG_LOCATION = CWD + "/logs/resource-info.csv";
    private int buildID;
    private int machineID;
    private int elapsedTimeSeconds = 0;
    private volatile boolean exit = false;
    private String os;
    private ResourceInterface monitor;

    public ResourceMonitor(int buildID, int machineID, String os){
        this.buildID = buildID;
        this.machineID = machineID;
        this.os = os;
        this.monitor = getMonitor();
    }

    public boolean setup(){
        return true;
    }

    public boolean start(){
        return true;
    }

    public boolean stop(){
        Log.info("Stopping the system resource thread.");
        Log.debug(Integer.toString(this.machineID));
        Log.debug(Integer.toString(this.buildID));
        exit = true;
        return true;
    }

    private void initLogFile(){
        File f = new File(LOG_LOCATION);
        if(f.exists()) {
            //noinspection ResultOfMethodCallIgnored
            f.delete();
        }
        try (FileWriter fw = new FileWriter(LOG_LOCATION)){
            fw.write("time_sec, memory_total_mb, memory_free_mb, cpu_usage\n");
        } catch (IOException e) {
            Log.error("Failed to create the resource log file " + e.toString());
            Thread.currentThread().interrupt();
        }
    }

    private ResourceInterface getMonitor(){
        if (this.os.toLowerCase().contains("linux")){
            return new Linux();
        }
        return new Windows();
    }

    private void sleep(){
        try {
            for (int i=0; i < 10 ; i++){
                Thread.sleep(1000);
                if (exit) break;
            }
        } catch (InterruptedException e) {
            Log.error("System monitor thread " + e.toString());
            Thread.currentThread().interrupt();
        }
    }

    private void appendToLog(Double cpu, Integer freeMemMB, Integer totMemMB){
        try (FileWriter fw = new FileWriter(LOG_LOCATION, true)){
            fw.write(elapsedTimeSeconds + ", " + totMemMB + ", " + freeMemMB + ", " + cpu + "\n");
        } catch (IOException e) {
            Log.error("Failed to write to " + LOG_LOCATION + " received the following error.");
            Log.error(e.toString());
        }
    }

    private void sendInfo(){
        // TODO send system information to raptor server.
    }

    @Override
    public void run() {
        Thread.currentThread().setName("ResourceMonitor");
        Log.info("Starting the system resource monitor thread.");
        initLogFile();

        CPUMonitor cpuMon = new CPUMonitor();
        Thread cpuMonThread = new Thread(cpuMon);
        cpuMonThread.start();
        while(!exit){
            Double cpuUsage = cpuMon.getCPUUsagePercentage();
            Integer freeMemKB = monitor.getFreeMemKB();
            Integer totalMemKB = monitor.getTotalMemKB();
            appendToLog(cpuUsage, freeMemKB/1024, totalMemKB/1024);
            sendInfo();
            elapsedTimeSeconds += 10;
            sleep();
        }
        Log.info("Stopping the system resource monitor thread.");
        cpuMon.finish();
    }
}
