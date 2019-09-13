package buildToolsConfigGenerator.config;

import java.util.HashMap;
import java.util.List;

/**
 * This class is used to generate the file that is used to configure Build Monitors proxy plugin list.
 */
public class BuildMonitorPluginList extends ConfigFile {

    BuildMonitorPluginList(String logLocation) {
        super(logLocation);
    }

    @Override
    public boolean generateConfigFiles(List<HashMap> fileInformation) {
        return false;
    }
}
