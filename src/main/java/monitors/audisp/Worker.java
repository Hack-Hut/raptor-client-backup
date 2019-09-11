package monitors.audisp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class Worker {

    public void loop() throws IOException{
        ServerSocket ss = new ServerSocket(4789);
        Socket client = ss.accept();
        worker(client);
    }

    private void worker(Socket client){
        try{
            InputStream inputStream = client.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String response = bufferedReader.readLine();
            while(response != null){
                response = bufferedReader.readLine();
                // do stuff with line here
            }
            client.close();
        }catch (IOException e){
            //handle exception
        }
    }
}
