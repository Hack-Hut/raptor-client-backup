package auditme;

import java.util.ArrayList;

public class Plugin {
    String path;
    String basePath;
    String selectedPlugin;
    String selectedPluginLocation;
    ArrayList<String> pluginNameList = new ArrayList<>();
    ArrayList<String> pluginScarNameList = new ArrayList<>();

    //TODO Add code for finding plugin

    Plugin(String path){
        this.path = path;
        this.basePath = utils.FileOperations.getBasePath(this.path);
        this.pluginNameList.add("gcc");
        this.pluginNameList.add("ld");
        this.pluginNameList.add("cc");
        this.pluginNameList.add("as");
        this.pluginNameList.add("objdump");
        this.pluginNameList.add("ranlib");
        this.pluginNameList.add("ar");
        this.pluginNameList.add("readelf");
    }

    /**
     * Finds the correct plugin (if any) for this.path.
     * @return PluginScarName.
     */
    public String get() {
        return "";
    }

    public static void main(String args[]) {
        String path = "/etc/passwd/";
        Plugin plugin = new Plugin(path);
    }
}
