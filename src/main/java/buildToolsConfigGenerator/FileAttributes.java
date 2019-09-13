package buildToolsConfigGenerator;

import java.util.HashMap;

/**
 * Give an executable, this class will populate the relevant information for it
 */
public class FileAttributes {

    private boolean shouldBeProxied;

    private String path;
    private String basePath;
    private String md5;
    private String sha256;
    private String plugin;

    public FileAttributes(String path) {
        this.path = path;
    }

    public void populateFileInfo() {
        this.shouldBeProxied = this.getShouldBeProxied();
        if (!this.shouldBeProxied) {
            return;
        }
        this.basePath = utils.FileOperations.getBasePath(this.path);
        this.md5 = this.getMd5();
        this.sha256 = this.getSha256();
        this.plugin = this.findPlugin();
    }

    public void showFileInfo() {
        if (!this.shouldBeProxied) {
            return;
        }
        System.out.println("Path:\t\t\t\t" + this.path);
        System.out.println("ShouldBeProxied:\t" + this.shouldBeProxied);
        System.out.println("MD5:\t\t\t\t" + this.md5);
        System.out.println("SHA256:\t\t\t\t" + this.sha256);
        System.out.println("Plugin:\t\t\t\t" + this.plugin);
        System.out.println();
    }

    public HashMap<String, String> getInfo(){
        HashMap<String, String> fileInfo = new HashMap<String, String>();
        fileInfo.put("Path", this.path);
        fileInfo.put("BasePath", this.basePath);
        fileInfo.put("MD5", this.md5);
        fileInfo.put("SHA256", this.sha256);
        fileInfo.put("Plugin", this.plugin);
        if (this.shouldBeProxied)fileInfo.put("shouldBeProxied", "true");
        else fileInfo.put("shouldBeProxied", "false");
        return fileInfo;
    }

    /**
     * Finds out if the file should be proxied or not
     * @return boolean
     */
    private boolean getShouldBeProxied() {
        // TODO Write code to see if the file should be proxied
        return utils.FileOperations.canRead(this.path);
    }

    /**
     * Finds what Scar plugin should be used (if any).
     * @return String: plugin name.
     */
    private String findPlugin() {
        //TODO Add code to find SCAR plugin.
        return "";
    }

    private String getMd5() {
        return utils.Hash.MD5.checksum(this.path);
    }

    private String getSha256() {
        return utils.Hash.SHA256.checksum(this.path);
    }
}
