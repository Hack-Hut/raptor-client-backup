package raptorClient.master;

import raptorClient.SetupJob;

import utils.Log;

public class Setup implements SetupJob {
    private int buildID;
    private String[] command;
    private String stage;
    private String os;

    public Setup(int buildID, String command[], String stage){
        this.buildID =  buildID;
        this.command = command;
        this.stage = stage;
        this.os = "linux";
    }
    public void start(){
        MasterController controller = getController();

        System.out.println();
        Log.debug("");
        Log.info("Setup Information");
        Log.info("--------------------------------------------------");
        controller.showSetup();

        System.out.println();
        Log.debug("");
        Log.info("Clean Machine");
        Log.info("--------------------------------------------------");
        controller.cleanMachine();      // Thread one

        System.out.println();
        Log.debug("");
        Log.info("Connecting to Slaves");
        Log.info("--------------------------------------------------");
        controller.connectToSlaves();   // Thread two

        System.out.println();
        Log.debug("");
        Log.info("Starting Monitors");
        Log.info("--------------------------------------------------");
        controller.startMonitor();

        System.out.println();
        Log.debug("");
        Log.info("Execute Build");
        Log.info("--------------------------------------------------");
        controller.executeBuild();

        System.out.println();
        Log.debug("");
        Log.info("Stopping Monitors");
        Log.info("--------------------------------------------------");
        controller.stopMonitor();

        System.out.println();
        Log.debug("");
        Log.info("Processing Results");
        Log.info("--------------------------------------------------");
        controller.processResults();

        System.out.println();
        Log.debug("");
        Log.info("Uploading Results");
        Log.info("--------------------------------------------------");
        controller.uploadResults();

        System.out.println();
        Log.debug("");
        Log.info("Killing Slaves");
        Log.info("--------------------------------------------------");
        controller.killSlaves();
    }

    private MasterController getController(){
        switch (stage){
            case "initial":
                return new Initial(buildID, stage, command);
            case "completeness":
                return new Completeness(buildID, stage, command);
            case "bm":
                return new BuildMonitor(buildID, stage, command);
            case "semmle":
                return new Semmle(buildID, stage, command);
            default:
                Log.error("Failed to find the correct mode");
                Log.error("Expected: initial, completeness, bm, semmle");
                Log.error("Actual: " + stage);
                System.exit(-1);
                return null;
        }
    }
}
