package auditme.config;

import java.util.HashMap;
import java.util.List;

public class BuildMonitorPluginList extends ConfigFile {

    BuildMonitorPluginList(String logLocation) {
        super(logLocation);
    }

    @Override
    public boolean generateConfigFiles(List<HashMap> fileInformation) {
        return false;
    }
}
