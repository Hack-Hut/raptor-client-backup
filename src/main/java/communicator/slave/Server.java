package communicator.slave;

import utils.Log;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Connects to the master build machine then listens for requests.
 * The requests are then forwarded to the relative handlers.
 * Note that the server communicates over TCP/IP rather than UDP
 *
 * Unlike the master server this server will only ever have one connection
 * and that will be to the master server. Therefore this class does not need
 * to to spawn a new thread for every connection, although this could be
 * an improvement for later.
 */
public class Server implements Runnable{
    final int PORT = 1337;
    private int buildID;
    volatile boolean exit = false;
    private final String CHAR_SET = "8859_1";

    public Server(int buildID){
        this.buildID = buildID;
    }

    public void start() throws IOException {
        ServerSocket ss = new ServerSocket(PORT);
        while (!exit){
            handleConnection(ss.accept());
        }
    }

    public void exit(){
        this.exit = true;
    }

    private boolean handleConnection(Socket client){
        try {
            System.out.println("Connection received");
            BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream(), CHAR_SET));
            OutputStream out = client.getOutputStream();
            PrintWriter pout = new PrintWriter(new OutputStreamWriter(out, CHAR_SET), true);
            out.write("HELLO MATE".getBytes());
            String request = in.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    public void run(){
        Thread.currentThread().setName("Slave Server");
        Log.debug("Spawning the slave server that will handle the requests made from the master server.");
        try {
            start();
        } catch (IOException e) {
            Log.error("Error in the slave web server");
            Log.error(e.toString());
        }
    }

    public static void main(String[] args){
        Server server = new Server(9);
        Thread thread = new Thread(server);
        thread.start();
    }
}
