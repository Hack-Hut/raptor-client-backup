package auditme.config;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public abstract class ConfigFile {

    private String logLocation;

    ConfigFile(String logLocation){
        this.logLocation = logLocation;
    }

    public abstract boolean generateConfigFiles(List<HashMap> fileInformation);

    boolean writeFile(String info) throws IOException {
        try (FileWriter fw = new FileWriter(logLocation)){
            fw.write(info);
            fw.close();
            return true;
        } catch (IOException e) {
            throw new IOException();
        }
    }
}
