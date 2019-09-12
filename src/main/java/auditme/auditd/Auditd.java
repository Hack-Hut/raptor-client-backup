package auditme.auditd;

import auditme.auditorParserInterface;
import auditme.config.*;
import utils.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * This class parses the auditd log files and generates the relevant configuration files for
 * Build Monitor
 */
public class Auditd implements auditorParserInterface {
    private String auditdLocation;
    private List<String> uniqueExes;
    private List<HashMap> buildExecutableInformation = new ArrayList<>();

    public Auditd(String auditdLocation) {
        this.auditdLocation = auditdLocation;
    }

    /**
     * This method will go through the unique executables and then find their,
     * md5, sha256, Scar plugin and determine if they need proxying.
     */
    @Override
    public void generateExecutableFileList() {
        for(String currentFile : this.uniqueExes){
            auditme.FileAttributes fileInfo = new auditme.FileAttributes(currentFile);
            fileInfo.populateFileInfo();
            fileInfo.showFileInfo();
            buildExecutableInformation.add(fileInfo.getInfo());
        }
    }

    @Override
    public boolean generateConfigurationFiles() {
        parseLog();
        generateExecutableFileList();
        generateExecutableFileList();
        return (generateConfigFile("BinaryHashList") &&
                generateConfigFile("BuildMonitorPluginList") &&
                generateConfigFile("BuildMonitorProxyList"));
    }

    /**
     * Note that this list contains the absolute paths of symbolic links.
     */
    private void parseLog()  {
        Set<String> uniqueExes = this.findUniqueExecutables();
        this.uniqueExes = utils.misc.convertSetToList(uniqueExes);
    }

    private Set<String> findUniqueExecutables(){
        Set<String> uniqueExecutables = new HashSet<>();
        String currentLine;
        File auditLog = new File(this.auditdLocation);
        try (BufferedReader buffer = new BufferedReader(new FileReader(auditLog))){
            if(!auditLog.canRead()) {
                buffer.close();
                throw new FileNotFoundException("The path " + this.auditdLocation + " does not have read permissions.");
            }
            while ((currentLine = buffer.readLine()) != null) {
                String auditdExecveSearchValue = "exe=";
                if(currentLine.contains(auditdExecveSearchValue)) {
                    uniqueExecutables.add(parseExecutableLine(currentLine));
                }
            }
        }
        catch (IOException e) {
            Log.error(e.toString());
        }
        return uniqueExecutables;
    }

    /**
     * Parses an Auditd line to extract the binaries that were executed.
     * @param auditdLine: line from the monitors.auditd log
     * @return the absolute path to the executable, or if it's a sym then it's target.
     * @throws IOException: if the file executable path cannot be converted to canonical path
     */
    private static String parseExecutableLine(String auditdLine) throws IOException {
        String[] split = auditdLine.split("exe=\"");
        if (split.length > 1) return cleanLine(split[1].split("\"")[0]);
        return "";
    }


    private static String cleanLine(String executable) throws IOException{
        File f = new File(executable);
        try {
            String realPath =  f.getCanonicalPath();
            String symTarget = utils.FileOperations.getSymLocation(realPath);
            if (symTarget.equals("")) {
                return realPath;
            }
            return symTarget;
        }
        catch (IOException e) {
            throw new IOException();
        }
    }

    private boolean generateConfigFile(String type){
        try{
            ConfigFile config = ConfigFactory.getConfig(type);
            return config.generateConfigFiles(buildExecutableInformation);
        } catch (ClassNotFoundException e) {
            Log.error("Failed to generate " + type);
        }
        return false;
    }
}