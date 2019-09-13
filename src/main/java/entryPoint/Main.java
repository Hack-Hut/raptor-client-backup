package entryPoint;

import utils.Log;

public class Main {
    private int buildID;
    private String mode;
    private String[] command;
    private String stage;
    private String downloadFile;

    public Main(String args[]){
        CommandLineParser parser = new CommandLineParser(args);
        buildID = parser.getId();
        mode = parser.getMode();
        command = parser.getCmd();
        stage = parser.getStage();
        downloadFile = parser.getDownloadFile();
    }

    private void banner(){
        Log.info(" ██████╗  █████╗ ██████╗ ████████╗ ██████╗ ██████╗");
        Log.info(" ██╔══██╗██╔══██╗██╔══██╗╚══██╔══╝██╔═══██╗██╔══██╗");
        Log.info(" ██████╔╝███████║██████╔╝   ██║   ██║   ██║██████╔╝");
        Log.info(" ██╔══██╗██╔══██║██╔═══╝    ██║   ██║   ██║██╔══██╗");
        Log.info(" ██║  ██║██║  ██║██║        ██║   ╚██████╔╝██║  ██║");
        Log.info(" ╚═╝  ╚═╝╚═╝  ╚═╝╚═╝        ╚═╝    ╚═════╝ ╚═╝  ╚═╝");
        Log.info(" --------------------------------------------------");
    }

    private boolean initLogger(){
        boolean res = Log.test();
        Log.clearLogs();
        Log.set(0);
        return res;
    }

    private void controlFlow(String[] args) {
        if(!initLogger()){
            System.out.println("Failed to write to logger at: " + Log.getLogLocation());
            System.exit(-1);
        }
        banner();
        if (mode.equals("master")){
            Log.debug("Starting in master mode.");
            stage.master.Setup setup = new stage.master.Setup(buildID, command, stage);
            setup.start();
        }
        else if (mode.equals("slave")){
            Log.debug("Starting in slave mode.");
            stage.slave.Setup setup = new stage.slave.Setup(buildID, command, stage);
            setup.start();
        }
        else {
            // TODO code for download mode
            System.out.println("DOWNLOAD MODE HERE");
        }
    }

    public static void main(String[] args){
        Main main = new Main(args);
        main.controlFlow(args);
    }
}
