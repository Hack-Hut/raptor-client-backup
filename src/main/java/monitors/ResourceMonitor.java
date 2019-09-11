package monitors;

import monitors.resourceMonitor.ResourceMonitorWorker;
import utils.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ResourceMonitor implements MonitorInterface {
    private static final String CWD = new File("").getAbsolutePath();
    private static final String LOG_LOCATION = CWD + "/logs/resource-info.csv";
    private ResourceMonitorWorker worker;

    public boolean setup(){
        initLogFile();
        return true;
    }

    public boolean start(){
        worker = new ResourceMonitorWorker(LOG_LOCATION);
        Thread workerThread = new Thread(worker);
        workerThread.start();
        return true;
    }

    public boolean stop(){
        Log.info("Stopping the system resource thread.");
        worker.stop();
        return true;
    }

    private void initLogFile(){
        File f = new File(LOG_LOCATION);
        if(f.exists()) {
            //noinspection ResultOfMethodCallIgnored
            f.delete();
        }
        try (FileWriter fw = new FileWriter(LOG_LOCATION)){
            fw.write("Time, MemoryUsageMB, MemoryFreeMB, MemoryTotalMB, PercentageCPU\n");
        } catch (IOException e) {
            Log.error("Failed to create the resource log file " + e.toString());
            Thread.currentThread().interrupt();
        }
    }
}
