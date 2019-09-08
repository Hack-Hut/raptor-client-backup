package auditme;

import java.util.HashMap;

/**
 * @author luke
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

    /**
     * Populates the relevant information for the file
     * @return false if the file should not be proxied.
     */
    public boolean populateFileInfo() {
        this.shouldBeProxied = this.getShouldBeProxied();
        if (!this.shouldBeProxied) {
            return false;
        }
        this.basePath = utils.FileOperations.getBasePath(this.path);
        this.md5 = this.getMd5();
        this.sha256 = this.getSha256();
        this.plugin = this.findPlugin();
        return true;
    }

    /**
     * Pretty prints the file info
     */
    public boolean showFileInfo() {
        if (!this.shouldBeProxied) {
            return false;
        }
        System.out.println("Path:\t\t\t\t" + this.path);
        System.out.println("ShouldBeProxied:\t" + this.shouldBeProxied);
        System.out.println("MD5:\t\t\t\t" + this.md5);
        System.out.println("SHA256:\t\t\t\t" + this.sha256);
        System.out.println("Plugin:\t\t\t\t" + this.plugin);
        System.out.println();
        return true;
    }

    /**
     * Places all the infomation gather about the file into
     * a HashMap data structure.
     * Contains the keys; path, md5, sha256 and plugin.
     * @return fileInfo HashMap
     */
    public HashMap<String, String> getInfo(){
        HashMap<String, String> fileInfo = new HashMap<String, String>();
        fileInfo.put("path", this.path);
        fileInfo.put("base path", this.basePath);
        fileInfo.put("md5", this.md5);
        fileInfo.put("sha256", this.sha256);
        fileInfo.put("plugin", this.plugin);
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
     * Finds the md5 checksum of the file
     * @return String
     */
    private String getMd5() {
        return utils.Hash.MD5.checksum(this.path);
    }

    /**
     * Finds the SHA256 checksum of the file
     * @return String
     */
    private String getSha256() {
        return utils.Hash.SHA256.checksum(this.path);
    }

    /**
     * Finds what Scar plugin should be used (if any).
     * @return String: plugin name.
     */
    private String findPlugin() {
        //TODO Add code to find SCAR plugin.
        return "";
    }
}
