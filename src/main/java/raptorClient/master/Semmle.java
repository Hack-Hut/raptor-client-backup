package raptorClient.master;

public class Semmle extends raptorClient.master.MasterController{
    public Semmle(int buildId, String stage, String[] buildCommand){
        super(buildId, stage, buildCommand);
    }
    public boolean startMonitor(){
        return true;
    }
    public boolean stopMonitor(){
        return true;
    }
    public boolean pingSlave(){
        return true;
    }
    public boolean cleanMachine(){
        return true;
    }
    public boolean uploadResults(){
        return true;
    }
    public boolean killSlaves(){
        return true;
    }
    public boolean processResults() {
        return  true;
    }
    public boolean connectToSlaves() {
        return  true;
    }
    public boolean executeBuild() {
        return  true;
    }

}
