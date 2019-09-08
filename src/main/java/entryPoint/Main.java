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

    private void controlFlow(String args[]) {
        Log.clearLogs();
        Log.set(0);
        banner();
        if (mode.equals("master")){
            Log.debug("Starting in master mode.");
            raptorClient.master.Setup setup = new raptorClient.master.Setup(buildID, command, stage);
            setup.start();
        }
        else if (mode.equals("slave")){
            Log.debug("Starting in slave mode.");
            raptorClient.slave.Setup setup = new raptorClient.slave.Setup(buildID, command, stage);
            setup.start();
        }
        else {
            // TODO code for download mode
            System.out.println("DOWNLOAD MODE HERE");
        }
    }

    public static void main(String args[]){
        Main main = new Main(args);
        main.controlFlow(args);
    }
}
