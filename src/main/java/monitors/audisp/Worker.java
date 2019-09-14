package monitors.audisp;

import utils.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Opens socket to listen to audisp-remote
 */
public class Worker implements Runnable{

    private Set<String> executables = new HashSet<>();
    private boolean isRunning = true;
    private boolean connectionEstablished = false;

    public void stop(){
        isRunning = false;
    }

    /**
     * Opens a socket then waits for the audit daemon initiate audisp-remote
     * audisp-remote, if all things well connects to the socket that was opened
     * This socket is kept throughout the build, and is handled by the worker method
     * The worker method parses the auditd logs in real time.
     *
     * @throws IOException If it fails to create a new port.
     */
    private void start() throws IOException{
        try (ServerSocket ss = new ServerSocket(4789)){
            Log.info("Audisp waiting for a connection on 127.0.0.1:4789");
            Socket client = ss.accept();
            connectionEstablished = true;
            Log.info("Audisp received connection.");
            worker(client);
        }catch (IOException e){
            Log.error("Failed to spawn Audisp local multiplexer server.");
            throw new IOException();
        }
    }

    private void worker(Socket client) throws IOException {
        try{
            Log.debug("Audisp-remote: Connection established");
            InputStream inputStream = client.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String response = bufferedReader.readLine();
            while(response != null && isRunning){
                parseString(response);
                response = bufferedReader.readLine();
            }
        }catch (IOException e){
            Log.error(e.toString());
        }
        client.close();
    }

    public boolean connectionEstablished(){
        return connectionEstablished;
    }

    public boolean test(){
        executeTestCommands();

        // Allow auditd startup to finish
        sleepThread();

        boolean uname = false;
        boolean whoami = false;
        boolean ls = false;
        for(String executable : executables){
            if (executable.contains("uname")) uname = true;
            if (executable.contains("whoami")) whoami = true;
            if (executable.contains("ls")) ls = true;
        }
        boolean res = (uname && whoami && ls);
        if(!res){
            Log.error("Expected to see \"uname\", \"whoami\" and \"ls\"");
            Log.error(executables.toString());
        }
        return res;
    }

    private void sleepThread(){
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            Log.error("Audisp worker thread was interrupted.");
        }
    }

    private void executeTestCommands(){
        Runtime r = Runtime.getRuntime();
        try {
            Process p = r.exec("uname");
            Process p2 = r.exec("whoami");
            Process p3 = r.exec("ls");
            p.waitFor();
            p2.waitFor();
            p3.waitFor();

        } catch (InterruptedException | IOException e) {
            Log.error("Failed to execute audisp test commands");
            Log.error(e.toString());
            Log.error(Arrays.toString(e.getStackTrace()));
        }
    }

    private void parseString(String auditLine){
        try {
            String end = auditLine.split("exe=\"")[1];
            executables.add(end.split("\"")[0]);
        }catch (ArrayIndexOutOfBoundsException ignored){}
    }

    public Object[] getExecutables(){
        return executables.toArray();
    }

    public void run(){
        utils.misc.setThreadName(Thread.currentThread(), this.getClass());
        try {
            start();
        } catch (IOException e) {
            Log.error("Audisp-remote failed with the following error");
            Log.error(e.toString());
        }
    }

    public static void main(String args[]) throws IOException {
        Worker worker = new Worker();
        worker.start();
    }
}
