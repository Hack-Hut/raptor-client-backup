package raptorClient.master;

import raptorClient.SetupJob;
import utils.Log;

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
        MasterController controller = getController();
        System.out.println();
        Log.debug("");
        Log.info("SETUP INFORMATION");
        Log.info("--------------------------------------------------");
        controller.showSetup();

        System.out.println();
        Log.debug("");
        Log.info("CLEAN MACHINE");
        Log.info("--------------------------------------------------");
        controller.cleanMachine();      // Thread one

        System.out.println();
        Log.debug("");
        Log.info("CONNECTING TO SLAVES");
        Log.info("--------------------------------------------------");
        controller.connectToSlaves();   // Thread two

        System.out.println();
        Log.debug("");
        Log.info("STARTING MONITORS");
        Log.info("--------------------------------------------------");
        controller.startMonitors();

        System.out.println();
        Log.debug("");
        Log.info("EXECUTE BUILD");
        Log.info("--------------------------------------------------");
        controller.executeBuild();

        System.out.println();
        Log.debug("");
        Log.info("STOPPING MONITORS");
        Log.info("--------------------------------------------------");
        controller.stopMonitors();

        System.out.println();
        Log.debug("");
        Log.info("PROCESSING RESULTS");
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
        ControllerFactory factory = new ControllerFactory(buildID, stage, command);
        try {
            return factory.get(this.stage);
        } catch (ClassNotFoundException e) {
            System.out.println("Failed to create the controller");
            System.exit(-1);
        }
        return null;
    }
}
