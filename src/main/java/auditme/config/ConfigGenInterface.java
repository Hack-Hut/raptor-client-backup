package auditme.config;

import java.util.HashMap;
import java.util.List;

public interface ConfigGenInterface {
    boolean generateConfigFiles(List<HashMap> fileInformation);
}
