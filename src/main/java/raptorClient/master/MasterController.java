package raptorClient.master;

import monitors.ResourceMonitor;
import raptorClient.findServer.ScanNetwork;
import utils.Exec;
import utils.Log;
import utils.Networking;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * This is the abstract class that forms the base functionality
 * for each of the different stages of the BEP build process.
 * The different build stages are:
 *
 * initial
 * completeness
 * Build-Monitor
 * Semmle
 */
public abstract class MasterController {

    private String localIP;
    private String serverIP;
    private List<String> slaveIPs;
    private int buildId;
    protected long pid;
    protected String stage;
    private String[] buildCommand;
    private String machineStatus;
    private String buildName;
    protected Map<String, String> env;
    protected String stdout;
    protected String stderr;
    long returnCode;
    protected String os;
    ResourceMonitor resourceMonitor;


    MasterController(int buildId, String stage, String[] buildCommand){
        this.buildId = buildId;
        this.stage = stage;
        this.buildCommand = buildCommand;
        this.os = utils.SystemOps.getOsType();
        localIP = Networking.getLocalIP();
        serverIP = getServerIP();
        slaveIPs = findSlaves();
        machineStatus = "TODO"; //TODO when api is done
        buildName = "TODO"; //TODO when api is done
        env = getEnvironmentVariables();
    }

    public abstract void startMonitors();
    public abstract void executeBuild();
    public abstract void stopMonitors();
    public abstract void processResults();
    public abstract void uploadResults();

    void showSetup(){
        Log.info("Stage: " + stage);
        Log.info("Controller: Master");
        Log.info("Build ID: " + buildId);
        Log.info("Build Command: " + Arrays.toString(buildCommand));
        Log.info("Local IP address:  " + localIP);
        Log.info("Raptor IP address: " + serverIP);
        Log.info("Slave IP addresses: " + slaveIPs);
        Log.info("Machine Status: " + machineStatus);
        Log.info("Build Name: " + buildName);
    }

    private String getServerIP(){
        ScanNetwork raptor = new ScanNetwork();
        ArrayList<String> onlineServers = raptor.getHosts();
        if (onlineServers.isEmpty()) return null;
        if (onlineServers.size() == 1) return onlineServers.get(0);
        Log.warn("Found multiple raptor servers running at the same time");
        Log.warn(onlineServers.toString());
        return onlineServers.get(0);
    }

    protected List<String> findSlaves(){
        return new ArrayList<>();
    }

    public void cleanMachine(){
    }

    public void connectToSlaves(){
    }

    public void killSlaves(){
    }

    /**Spawn a new thread to execute a command in the background
     * @return The class that is executing the thread
     */
    Exec executeCommand() {
        Exec execution = new Exec(buildCommand);
        Thread execThread = new Thread(execution);
        execThread.start();
        sleepMainThread(100);
        return execution;
    }

    void sleepMainThread(int time){
        try {
            Log.debug("Sleeping main thread for " + time + " milliseconds.");
            Thread.sleep(time);
        } catch(InterruptedException e) {
            Log.error("main.java.entryPoint.Main thread sleep got interrupted.");
            Thread.currentThread().interrupt();
        }
    }

    void startResourceMonitor(){
        resourceMonitor = new ResourceMonitor();
        resourceMonitor.setup();
        resourceMonitor.start();
    }

    void stopResourceMonitor(){
        resourceMonitor.stop();
    }

    void logRunningThreads(){
        if (this.stage.contains("initial")) {
            Log.debug("Sleeping for one second to let resource monitor thread die.");
            sleepMainThread(1);
        }
        Log.debug("Currently, the following threads are running");
        for(Thread currentThread: Thread.getAllStackTraces().keySet()){
            Log.debug(currentThread.toString());
        }
    }
    private Map<String, String> getEnvironmentVariables(){
        env = System.getenv();
        for (String envName : env.keySet()) {
            Log.debug(envName, env.get(envName));
        }
        return env;
    }
}
