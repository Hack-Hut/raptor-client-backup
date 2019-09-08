package utils;

import java.io.File;

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

}
