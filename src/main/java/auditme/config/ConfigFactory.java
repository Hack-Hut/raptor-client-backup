package auditme.config;

import utils.Log;

import java.io.File;

/**
 * Configuration File factory:
 * BinaryHashList
 * BuildMonitorPluginList
 * BuildMonitorProxyList
 */
public class ConfigFactory {
    public static ConfigFile getConfig(String type) throws ClassNotFoundException {
        String logDir = new File("").getAbsolutePath() + "/logs/";
        if (type.equals("BinaryHashList")){
            return new BinaryHashList(logDir + type + ".json");
        }

        else if (type.equals("BuildMonitorPluginList")){
            return new BuildMonitorProxyList(logDir + type + ".toml");
        }

        else if (type.equals("BuildMonitorProxyList")){
            return new BuildMonitorProxyList(logDir + type + ".toml");
        }

        Log.error("Failed to find the correct config type for " + type);
        throw new ClassNotFoundException("Failed to find correct config type.");
    }
}
