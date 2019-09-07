package monitors.resourceMonitor;

import utils.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Linux implements ResourceInterface {
    private final static String memLoc = "/proc/meminfo";

    public Integer getFreeMemKB(){
        ArrayList<String> memInfo = readFile();
        Integer memKB = 0;
        for (String line : memInfo){
            if (line.contains("MemFree:")){
                memKB = parseMemLine(line);
            }
        }
        return memKB;
    }

    public Integer getTotalMemKB(){
        ArrayList<String> memInfo = readFile();
        Integer memKB = 0;
        for (String line : memInfo){
            if (line.contains("MemTotal:")){
                memKB = parseMemLine(line);
            }
        }
        return memKB;
    }


    private ArrayList<String> readFile(){
        File f = new File(Linux.memLoc);
        ArrayList<String> lines = new ArrayList<>();

        try {
            BufferedReader b = new BufferedReader(new FileReader(f));
            String currentLine;
            while ((currentLine = b.readLine()) != null){
                lines.add(currentLine);
            }
        } catch (IOException e) {
            Log.error("File at location " + memLoc + " does not exist");
            Log.error(e.toString());
        }
        return lines;
    }

    private Integer parseMemLine(String line) {
        boolean colonFound = false;
        boolean intFound = false;
        StringBuilder mem = new StringBuilder();
        for (char current : line.toCharArray()){
            if ((intFound) & (current == ' ')) break;
            if ((colonFound) & (current != ' ')){
                intFound = true;
                mem.append(current);
            }
            if (current == ':') colonFound = true;

        }
        try {
            return Integer.parseInt(mem.toString());
        }catch (NumberFormatException e){
            Log.warn("Failed to parse System resource memory line " +  line);
            Log.warn("Current calculated value " + mem.toString());
            Log.warn("colonFound: " + colonFound);
            Log.warn("intFound: " + intFound);
            return 0;
        }
    }


}
