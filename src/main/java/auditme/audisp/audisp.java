package auditme.audisp;

import auditme.auditorParserInterface;
import auditme.config.*;
import utils.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class audisp implements auditorParserInterface {
    private Object[] executableList;
    private List<HashMap> buildExecutableInformation = new ArrayList<>();

    public audisp(Object[] executables){
        this.executableList = executables;
    }

    @Override
    public boolean generateConfigurationFiles() {
        generateExecutableFileList();
        return (generateConfigFile("BinaryHashList") &&
                generateConfigFile("BuildMonitorPluginList") &&
                generateConfigFile("BuildMonitorProxyList"));
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

    public void showParsedResults(){
        for(HashMap currentFile: buildExecutableInformation){
            Log.debug(currentFile.toString());
        }
    }

    private void generateExecutableFileList(){
        for(Object currentPath : executableList){
            String current = currentPath.toString();
            auditme.FileAttributes fileInfo = new auditme.FileAttributes(current);
            fileInfo.populateFileInfo();
            buildExecutableInformation.add(fileInfo.getInfo());
        }
    }
}
