package utils;



import java.net.InetAddress;
import java.net.UnknownHostException;

public class Networking {
    public static String getLocalIP(){
        InetAddress ip;
        try {
            ip = InetAddress.getLocalHost();
            return ip.getHostAddress();

        } catch (UnknownHostException e) {
            Log.error("Failed to get the local IP address");
            Log.error(e.toString());
            e.printStackTrace();
            return "";
        }
    }
}

