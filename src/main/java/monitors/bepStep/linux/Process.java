package monitors.bepStep.linux;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class Process {
    private int pid;
    private int ppid;
    private String procLocation;
    private String fileDescriptorLocation;
    private String environmentLocation;
    private String statLocation;
    private String commandlineLocation;
    private String currentWorkingDirLocation;

    public Process(long pid){
        pid = (int) pid;
        procLocation = "/proc/" + pid + "/";
        fileDescriptorLocation = procLocation + "fd";
        environmentLocation = procLocation + "environ";
        statLocation = procLocation + "stat";
        commandlineLocation = procLocation + "cmdline";
        currentWorkingDirLocation = procLocation + "cwd";
    }

    public boolean isRunning(){
        File proc = new File(procLocation);
        return proc.exists();
    }

    public String getEnviron(){
        return readProcFile(environmentLocation, '\n');
    }

    public String getCmd(){
        return readProcFile(commandlineLocation, ' ');
    }

    public Integer getPpid(){
        //(4) ppid  %d
        String statFile = readProcFile(statLocation, ' ');

        int tokens = 0;
        StringBuilder ppidBuilder = new StringBuilder();
        for (int x =0 ; x <= statFile.length() -1; x++){
            char currentChar = statFile.charAt(x);
            if ((tokens == 3) & (currentChar != ' ')) {
                ppidBuilder.append(currentChar);
            }
            else if (tokens == 4) break;
            if (currentChar == ' ') tokens ++;
        }
        String ppid = String.valueOf(ppidBuilder);
        try{
            return Integer.valueOf(ppid);
        }catch (NumberFormatException e){
            return 0;
        }
    }

    public ArrayList<String> getDescriptors(){
        ArrayList<String> paths = new ArrayList<>();
        File fd = new File(fileDescriptorLocation);
        File[] fds = fd.listFiles();
        if (fds == null) return paths;
        for (File f: fds){
            try { paths.add(f.getCanonicalPath());}
            catch (IOException e) { paths.add(e.toString());}
        }
        return paths;
    }

    public static ArrayList <Integer> getChildren(Integer pid){
        ArrayList<Integer> children = new ArrayList<>();
        ArrayList<Integer> runningProcesses = getListOfRunningProcesses();

        for (Integer current : runningProcesses){
            Process newProcess = new Process(current);
            if (newProcess.getPpid().equals(pid)) children.add(current);
        }
        return children;
    }

    public String getCwd(){
        File cwdFile = new File(currentWorkingDirLocation);
        try {
            return cwdFile.toPath().toRealPath().toString();
        } catch (IOException e) {
            return e.toString();
        }
    }

    public static ArrayList<Integer> getListOfRunningProcesses(){
        ArrayList<Integer> runningProceses = new ArrayList<>();
        File proc = new File("/proc");
        File[] processes = proc.listFiles();
        assert processes != null;
        for (File f: processes){
            if(f.isDirectory()){
                String processName = f.getName();
                try  {runningProceses.add(Integer.parseInt(processName));}
                catch(NumberFormatException ignored){}
            }
        }
        return runningProceses;
    }

    private String readProcFile(String location, char replaceChar) {
        StringBuilder data = new StringBuilder();
        FileInputStream fileInputStream = null;
        try {fileInputStream = new FileInputStream(location);} catch (FileNotFoundException ignored) { return ""; }

        //specify UTF-8 encoding explicitly
        try (InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, StandardCharsets.UTF_8)) {

            int singleCharInt;
            char singleChar;
            while((singleCharInt = inputStreamReader.read()) != -1) {
                singleChar = (char) singleCharInt;
                if (singleChar == '\0'){
                    singleChar = replaceChar;
                }
                data.append(singleChar);
            }
        } catch (IOException e) {
            return e.toString();
        }
        return data.toString();
    }

    public static void main(String args[]){
        Process test = new Process(22025);
        System.out.println(test.getEnviron());
        System.out.println(test.getPpid());
        System.out.println(test.getCmd());
        System.out.println(test.getDescriptors());

    }
}
