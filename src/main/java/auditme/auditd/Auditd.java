package auditme.auditd;

import auditme.auditorParserInterface;
import auditme.config.BinaryHashList;
import auditme.config.BuildMonitorPluginList;
import auditme.config.BuildMonitorProxyList;
import auditme.config.ConfigGenInterface;
import utils.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * @author luke.goddard
 * This class is used to handle Auditd log files.
 */
public class Auditd implements auditorParserInterface {
    private String auditdLocation;
    private List<String> uniqueExes;
    private List<HashMap> buildExecutableInformation = new ArrayList<>();

    private BinaryHashList hashListMaker;
    private BuildMonitorPluginList pluginMaker;
    private BuildMonitorProxyList proxyMaker;
    private boolean configurationSuccessful = true;

    public Auditd(String auditdLocation) {
        this.auditdLocation = auditdLocation;
    }

    /**
     * Parses the Auditd log and then returns a unique list of executables that were found,
     * Note that this list contains the absolute paths of symbolic links.
     * @return Unique list of executables
     */
    private List<String> parseLog()  {
        Set<String> uniqueExes = this.findUniqueExecutables();
        this.uniqueExes = utils.misc.convertSetToList(uniqueExes);
        return this.uniqueExes;
    }

    /**
     * This method will go through the unique executables and then find their,
     * md5, sha256, Scar plugin and determine if they need proxying.
     *
     * WARNING, will throw NullPointerException, if parse is not called
     * first
     * @return A list containing HashMaps for each file.
     * @throws NullPointerException
     */
    private List<HashMap> populateFileInformation() {
        for(String currentFile : this.uniqueExes){
            auditme.FileAttributes fileInfo = new auditme.FileAttributes(currentFile);
            fileInfo.populateFileInfo();
            fileInfo.showFileInfo();
            buildExecutableInformation.add(fileInfo.getInfo());
        }
        return buildExecutableInformation;
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

    @Override
    public boolean generateConfigurationFiles() {
        parseLog();
        populateFileInformation();
        getConfigMakers();

        generateConfiguration(hashListMaker, "BinaryHashList.json");
        generateConfiguration(pluginMaker, "PluginConfiguration.toml");
        generateConfiguration(proxyMaker, "ProxyConfiguration.toml");

        return configurationSuccessful;
    }

    private void getConfigMakers(){
        getProxyConfigMaker();
        getPlugingConfigMaker();
        getBinaryListMaker();
    }

    private void getProxyConfigMaker(){
        proxyMaker = new BuildMonitorProxyList();
    }

    private void getPlugingConfigMaker(){
        pluginMaker = new BuildMonitorPluginList();
    }

    private void getBinaryListMaker(){
        hashListMaker = new BinaryHashList();
    }

    private void generateConfiguration(ConfigGenInterface service, String name){
        if (!service.generateConfigFiles(buildExecutableInformation)){
            Log.error("Failed to generate " + name + ".");
            configurationSuccessful = false;
        }
        else {
            Log.info("Successfully generated " + name + ".");
        }
    }
}