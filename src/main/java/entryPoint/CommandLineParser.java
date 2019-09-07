package entryPoint;

import docopt.Docopt;

import java.util.Map;

public final class CommandLineParser {
    private Map<String, Object> opts;
    private String mode;
    private String stage;
    private String id;
    private String[] cmd;
    private String downloadFile;

    private static final String doc =
            " ██████╗  █████╗ ██████╗ ████████╗ ██████╗ ██████╗\n"
            + " ██╔══██╗██╔══██╗██╔══██╗╚══██╔══╝██╔═══██╗██╔══██╗\n"
            + " ██████╔╝███████║██████╔╝   ██║   ██║   ██║██████╔╝\n"
            + " ██╔══██╗██╔══██║██╔═══╝    ██║   ██║   ██║██╔══██╗\n"
            + " ██║  ██║██║  ██║██║        ██║   ╚██████╔╝██║  ██║\n"
            + " ╚═╝  ╚═╝╚═╝  ╚═╝╚═╝        ╚═╝    ╚═════╝ ╚═╝  ╚═╝\n"
            + " --------------------------------------------------\n"
            + "Raptor-Client.\n"
            + "\n"
            + "Usage:\n"
            + "  raptor-client --master --stage=<stage> --id=<id> --cmd=<command>\n"
            + "  raptor-client --slave --id=<id>\n"
            + "  raptor-client --download=<file>\n"
            + "  raptor-client --help\n"
            + "  raptor-client --version\n"
            + "\n"
            + "Options:\n"
            + "  --master                        Master node.\n"
            + "  --slave                         Slave node.\n"
            + "  -s=<stage> --stage=<stage>      inital |completeness | semmle | bm.\n"
            + "  --id=<id>                       Build id.\n"
            + "  --cmd=<command>                 Build command.\n"
            + "  --download=<file>               bm | config"
            + "  -h --help                       Show this screen.\n"
            + "  --version                       Show version.\n"
            + "\n"
            + "Examples.\n"
            + "  raptor-client --master --stage=initial --id=123, --cmd='python3 /usr1/code/build.py'\n"
            + "  raptor-client --slave --id=123'\n";

    CommandLineParser(String[] args){
        this.opts = new Docopt(doc).withVersion("BETA-1.0").parse(args);
        this.mode = findMode();
        this.stage = (String) opts.get("--stage");
        this.id = (String) opts.get("--id");
        this.cmd = parseCmd((String) opts.get("--cmd"));
        this.downloadFile = (String) opts.get("--download");
    }

    private String findMode(){
        if((boolean) opts.get("--slave")){
            return "slave";
        }
        else if ((boolean) opts.get("--master")) {
            return "master";
        }
        return "download";
    }

    private String[] parseCmd(String command){
        return command.split("\\s+");
    }

    String getMode(){
        return mode;
    }

    public String getStage(){
        return stage;
    }

    Integer getId(){
        try {
            return Integer.parseInt(id);
        } catch(NumberFormatException e) {
            System.out.println("--id must be an integer.");
            System.exit(1);
            return 0;
        }
    }

    String[] getCmd(){
        if (cmd == null){
            System.out.println("Command cannot be null");
            System.exit(1);
        }
        return cmd;
    }

    String getDownloadFile(){
        return downloadFile;
    }

    public static void main(String[] args) {
        CommandLineParser parser = new CommandLineParser(args);
    }
}
