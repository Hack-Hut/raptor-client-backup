package raptorClient.findServer;

import utils.Log;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

/**
 * This class is used to find hosts on the current network. These hosts could be
 * the raptor-web-server, raptor-master-nodes or raptor-slave-nodes. By trying
 * to perform a TCP handshake with the remote host on a predefined port, we
 * can test to see if the host is online.
 *
 * Note, additional checks need to be done after finding hosts with x open port.
 * This is to make sure that the host is actually the write host we're trying to
 * communicate with.
 */
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
