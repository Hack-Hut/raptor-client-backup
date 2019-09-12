package auditme.audisp;

import auditme.auditorParserInterface;
import auditme.config.*;
import utils.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *  This class takes a list of files from the audisp monitor worker
 *  and generates the relevant Build-Monitor configuration files
 */
public class audisp implements auditorParserInterface {
    private Object[] executableList;
    private List<HashMap> buildExecutableInformation = new ArrayList<>();

    public audisp(Object[] executables){
        this.executableList = executables;
    }

    /**
     * Generates the configuration required for build monitor and the proxy kernel module
     * @return success
     */
    @Override
    public boolean generateConfigurationFiles() {
        generateExecutableFileList();
        return (generateConfigFile("BinaryHashList") &&
                generateConfigFile("BuildMonitorPluginList") &&
                generateConfigFile("BuildMonitorProxyList"));
    }

    /**
     * For each executable in the file list, this will generate information about each
     * of the files.
     */
    public void generateExecutableFileList(){
        for(Object currentPath : executableList){
            String current = currentPath.toString();
            auditme.FileAttributes fileInfo = new auditme.FileAttributes(current);
            fileInfo.populateFileInfo();
            buildExecutableInformation.add(fileInfo.getInfo());
        }
    }

    /**
     * Debugging method
     */
    private void showParsedResults(){
        for(HashMap currentFile: buildExecutableInformation){
            Log.debug(currentFile.toString());
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
