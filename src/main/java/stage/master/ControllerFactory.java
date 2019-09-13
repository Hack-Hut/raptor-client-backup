package stage.master;

import utils.Log;

public class ControllerFactory {
    private int buildId;
    String stage;
    String[] buildCommand;

    ControllerFactory(int buildId, String stage, String[] buildCommand){
        this.buildId = buildId;
        this.stage = stage;
        this.buildCommand = buildCommand;
    }

    public MasterController get(String type) throws ClassNotFoundException {
        switch (type){
            case "initial":
                return new Initial(buildId, stage, buildCommand);
            case "Semmle":
                return new Semmle(buildId, stage, buildCommand);
            case "Completeness":
                return new Completeness(buildId, stage, buildCommand);
            case "BuildMonitor":
                return new BuildMonitor(buildId, stage, buildCommand);
            default:
                Log.error("Failed to find the correct mode");
                Log.error("Expected: initial, completeness, bm, semmle");
                Log.error("Actual: " + stage);
                System.exit(-1);
                return null;
        }
    }
}
