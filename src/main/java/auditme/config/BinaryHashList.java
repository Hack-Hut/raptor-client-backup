package auditme.config;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

/**
 * This class is used to generate the configuration file called BinaryHashList.json
 * This file is used to populate a database of known "safe" to proxy binaries,
 * for the proxy kernel module
 */
public class BinaryHashList extends ConfigFile {

    BinaryHashList(String logLocation) {
        super(logLocation);
    }

    @Override
    public boolean generateConfigFiles(List<HashMap> fileInformation) {
        StringBuilder json = new StringBuilder();
        for(HashMap fileInfo: fileInformation){
            String infoJson = fileInfo.toString();
            json.append(infoJson).append("\n");
        }
        try{
            this.writeFile(json.toString());
        } catch (IOException e) {
            return false;
        }
        return true;
    }
}
