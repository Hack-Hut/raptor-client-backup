package raptorClient.master;

import raptorClient.findServer.ScanNetwork;
import monitors.resourceMonitor.ResourceMonitor;
import utils.Exec;
import utils.Log;
import utils.Networking;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public abstract class MasterController {

    protected String localIP;
    protected String serverIP;
    protected List<String> slaveIPs;
    protected int buildId;
    protected long pid;
    protected String stage;
    protected String[] buildCommand;
    protected String machineStatus;
    protected String buildName;
    protected Map<String, String> env;
    protected String stdout;
    protected String stderr;
    protected long returnCode;
    protected String os;
    protected ResourceMonitor resourceMonitor;


    public MasterController(int buildId, String stage, String[] buildCommand){
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
        resourceMonitor = new ResourceMonitor(buildId, 0, os);
    }

    public void showSetup(){
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

    private Map<String, String> getEnvironmentVariables(){
        env = System.getenv();
        for (String envName : env.keySet()) {
            Log.debug(envName, env.get(envName));
        }
        return env;
    }

    public boolean cleanMachine(){
        return true;
    }

    public boolean connectToSlaves(){
        return true;
    }

    public boolean killSlaves(){
        return true;
    }

    /**Spawn a new thread to execute a command in the background
     * @return The class that is executing the thread
     */
    public Exec executeCommand() {
        Exec execution = new Exec(buildCommand);
        Thread execThread = new Thread(execution);
        execThread.start();
        sleepMainThread(100);
        return execution;
    }

    protected void sleepMainThread(int time){
        try {
            Log.debug("Sleeping main thread for " + time + " milliseconds.");
            Thread.sleep(time);
        } catch(InterruptedException e) {
            Log.error("main.java.entryPoint.Main thread sleep got interrupted.");
            Thread.currentThread().interrupt();
        }
    }

    protected void startSysResourceMonitor(){
        Thread monitorThread = new Thread(resourceMonitor);
        monitorThread.start();
    }

    protected void stopSysResourceMonitor(){
        resourceMonitor.finish();
    }

    public abstract boolean startMonitor();    //Start the Monitoring tools
    public abstract boolean executeBuild();    //Execute the build
    public abstract boolean stopMonitor();     //Stop the monitoring tools
    public abstract boolean processResults();  //Process the results
    public abstract boolean uploadResults();   //Upload the results to the server
}
