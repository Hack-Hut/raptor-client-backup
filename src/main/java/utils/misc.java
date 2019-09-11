package utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class misc {
    public static <T> List<String> convertSetToList(Set<T> set)
    {
        List<String> aList = new ArrayList<>(set.size());
        for (T x : set)
            aList.add((String) x);
        return aList;
    }

    public static void setThreadName(Thread thread, Class currentClass){
        String name = currentClass.getSimpleName();
        Log.debug("Starting the " + name + " thread.");
        thread.setName(name);
    }

    public static void showLogFileList() {
        String cwd = new File("").getAbsolutePath();
        String logLocation = cwd + "/logs/";
        for(String log : utils.SystemOps.ls(logLocation)){
            Log.info("Successfully generated " + log + ".");
        }
    }
}
