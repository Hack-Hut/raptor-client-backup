package auditme.config;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

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
