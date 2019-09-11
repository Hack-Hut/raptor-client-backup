package utils;


import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class Networking {
    public static String getLocalIP(){

        try {
            Enumeration Interfaces = NetworkInterface.getNetworkInterfaces();
            while (Interfaces.hasMoreElements()) {
                NetworkInterface Interface = (NetworkInterface) Interfaces.nextElement();
                Enumeration Addresses = Interface.getInetAddresses();
                while (Addresses.hasMoreElements()) {
                    InetAddress Address = (InetAddress) Addresses.nextElement();
                    if (Address.getHostAddress().contains("192.")) {
                        return Address.getHostAddress();
                    }

                }
            }
        }catch (SocketException e){
            Log.error("Failed getting the local IP address.");
            Log.error(e.toString());
        }
        return "";
    }
}

