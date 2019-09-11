package auditme.audisp;

import auditme.auditorParserInterface;
import auditme.config.BinaryHashList;
import auditme.config.BuildMonitorPluginList;
import auditme.config.BuildMonitorProxyList;
import auditme.config.ConfigGenInterface;
import utils.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class audisp implements auditorParserInterface {
    private Object[] executableList;
    private List<HashMap> buildExecutableInformation = new ArrayList<>();

    private BinaryHashList hashListMaker;
    private BuildMonitorPluginList pluginMaker;
    private BuildMonitorProxyList proxyMaker;
    private boolean configurationSuccessful = true;

    public audisp(Object[] executables){
        this.executableList = executables;
    }

    @Override
    public boolean generateConfigurationFiles() {
        generateExecutableFileList();
        getConfigMakers();
        generateConfiguration(hashListMaker, "BinaryHashList.dict");
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

    public void generateExecutableFileList(){
        for(Object currentPath : executableList){
            String current = currentPath.toString();
            auditme.FileAttributes fileInfo = new auditme.FileAttributes(current);
            fileInfo.populateFileInfo();
            buildExecutableInformation.add(fileInfo.getInfo());
        }
    }

    public void showParsedResults(){
        for(HashMap currentFile: buildExecutableInformation){
            Log.debug(currentFile.toString());
        }
    }
}
