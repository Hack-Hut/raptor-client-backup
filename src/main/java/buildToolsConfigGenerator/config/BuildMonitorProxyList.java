package buildToolsConfigGenerator.config;

import java.util.HashMap;
import java.util.List;

/**
 * This class is used to generate the file that Build Monitor uses to know what binaries
 * require what plugin
 */
public class BuildMonitorProxyList extends ConfigFile {

    BuildMonitorProxyList(String logLocation) {
        super(logLocation);
    }

    @Override
    public boolean generateConfigFiles(List<HashMap> fileInformation) {
        return false;
    }
}
