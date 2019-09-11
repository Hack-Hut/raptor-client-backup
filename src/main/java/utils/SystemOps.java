package utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;

public class SystemOps {

    /** Finds the number of CPU cores for the given target.
     * @return Int: number of cores.
     */
    public static int getCPUCores() {
        return Runtime.getRuntime().availableProcessors();
    }

    public static String getOsType(){
        return System.getProperty("os.name");
    }

    public static String getCWD(){
       return new File("").getAbsolutePath();
    }

    public static ArrayList<String> ls(String path){
        File dir = new File(path);
        File[] filesList = dir.listFiles();
        ArrayList<String> files = new ArrayList<>();
        if (filesList == null) return files;
        for (File f : filesList) {
            if( f.isFile() ){
                files.add(f.getName());
            }
        }
        return files;
    }
}
