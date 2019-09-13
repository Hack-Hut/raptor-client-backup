package findServer;

import utils.Log;
import utils.Networking;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * This class finds the current hosts network LAN address, the
 * one starting with "192." Then populates a list of all hosts
 * in the subnet, every host in that subnet is added to a thread
 * pool. Each item in the thread pool is then executed with the
 * FindHost class. All the hosts that responded are returned as a
 * list.
 */
public class ScanNetwork {
    private static final int MAX_T = utils.SystemOps.getCPUCores();

    private String localIpAdr;
    private String baseIp;
    private String[] localSubnetList;

    public ScanNetwork(){
        this.localIpAdr = Networking.getLocalIP();
        this.baseIp = this.getBaseIp();
        this.localSubnetList = this.getSubnetIpList();
    }

    public ArrayList<String> getHosts(){
        ArrayList<FindHost> threadPool = new ArrayList<>();
        ArrayList<String> onlineHosts = new ArrayList<>();
        ScanNetwork networkScan = new ScanNetwork();
        Log.debug("Spawning 255 threads with thread pool size " + MAX_T + " to find remote host.");
        for(String ip : networkScan.localSubnetList) threadPool.add(new FindHost(ip, 80));
        ExecutorService pool = Executors.newFixedThreadPool(MAX_T);
        for(FindHost thread: threadPool) pool.execute(thread);
        pool.shutdown();
        try {
            pool.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Log.error("Failed wait for all threads in the findMachine thread pool to terminate");
            Log.error(e.toString());
            Thread.currentThread().interrupt();
        }
        Log.debug("All 255 threads have exited.");
        for(FindHost thread: threadPool) {
            String online = thread.isHostOnline();
            if (online != null){
                onlineHosts.add(online);
            }
        }
        return onlineHosts;
    }

    /**
     * creates a /24 ip address list for the local ip.
     * @return List of ip addresses
     */
    private String[] getSubnetIpList(){
        String[] localSubnetList = new String[255];
        for(int i = 1; i <= 255; i++) {
            localSubnetList[i - 1] = this.baseIp + i;
        }
        return localSubnetList;
    }

    /**
     * Given an ip address for example 192.168.181.55 this method will return 192.168.181.
     * @return ip address with the last octet missing
     */
    private String getBaseIp() {
        int octetCount = 0;
        StringBuilder baseIp = new StringBuilder();

        for(int i=0; i < this.localIpAdr.length(); i ++) {
            char currentChar = this.localIpAdr.charAt(i);
            if(currentChar == '.') {
                octetCount ++;
            }
            baseIp.append(currentChar);
            if(octetCount == 3){
                break;
            }
        }
        return baseIp.toString();
    }
}
