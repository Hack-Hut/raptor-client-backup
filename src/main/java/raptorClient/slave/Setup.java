package raptorClient.slave;

import raptorClient.SetupJob;

public class Setup implements SetupJob {
    private int buildID;
    private String[] command;
    private String stage;

    public Setup(int buildID, String[] command, String stage){
        this.buildID =  buildID;
        this.command = command;
        this.stage = stage;
    }
    public void start(){

    }
}
