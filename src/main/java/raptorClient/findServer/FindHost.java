package raptorClient.findServer;

import utils.Log;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

public class FindHost implements Runnable{

    private String ip;
    private int rport;
    private boolean found = false;

    FindHost(String ip, int rport){
        this.ip = ip;
        this.rport = rport;
    }

    String isHostOnline(){
        if (found){
            return ip;
        }
        return null;
    }

    /**
     * This method will try and connect to the host on rport
     * @return: Boolean true or false
     */
    private boolean serverListening(){
        SocketAddress sockaddr = new InetSocketAddress(this.ip, this.rport);
        try (Socket socket = new Socket()) {
            socket.connect(sockaddr, 10); //timeout
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void run(){
        if (serverListening()){
            found = true;
            Log.info("Found host at "  + this.ip + ":" + this.rport);
        }
    }
}
