package masterNode;

import java.util.ArrayList;
import java.util.List;

interface MasterNodeInterface {
    List<String> findSlaves();
    int findSlaveBuildID(String ip);
    void tellSlavesToStartMonitor();
    void tellSlavesToStopMonitor();
    boolean pingSlaves();
    boolean askSlavesIfJobCompletedOk();
}

public class MasterNode implements MasterNodeInterface {
    public MasterNode(){

    }

    public List<String> findSlaves(){
        List<String> ipList = new ArrayList<>();
        return ipList;
    }

    public int findSlaveBuildID(String ip) {
        return 0;
    }

    public void tellSlavesToStartMonitor(){

    }

    public void tellSlavesToStopMonitor(){

    }

    public boolean pingSlaves(){
       return true;
    }

    public boolean askSlavesIfJobCompletedOk(){
        return true;
    }


}
