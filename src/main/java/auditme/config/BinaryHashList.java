package auditme.config;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class BinaryHashList implements ConfigGenInterface {
    private static String cwd = new File("").getAbsolutePath();
    static final String logLocation = cwd + "/logs/BinaryHashList.dict";

    @Override
    public boolean generateConfigFiles(List<HashMap> fileInformation) {
        StringBuilder json = new StringBuilder();
        for(HashMap fileInfo: fileInformation){
            String infoJson = fileInfo.toString();
            json.append(infoJson).append("\n");
        }
        try{
            writeFile(json.toString());
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    private boolean writeFile(String info) throws IOException{
        try {
            FileWriter fw = new FileWriter(logLocation);
            fw.write(info);
            fw.close();
            return true;
        } catch (IOException e) {
            throw new IOException();
        }
    }
}
