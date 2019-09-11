package monitors.resourceMonitor;

import utils.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

interface MemoryUsageInterface {
    int getUsedMemory();
    int getFreeMemory();
    int getTotalMemory();
}

class MemoryUsageLinux implements MemoryUsageInterface {
    private final static String MEM_LOC = "/proc/meminfo";
    private int totalMemoryMB = 0;
    private int freeMemoryMB = 0;

    @Override
    public int getUsedMemory() {
        return totalMemoryMB - freeMemoryMB;
    }

    @Override
    public int getFreeMemory() {
        ArrayList<String> memInfo = readFile();
        int memKB = 0;
        for (String line : memInfo){
            if (line.contains("MemFree:")){
                memKB = parseMemLine(line);
            }
        }
        freeMemoryMB = memKB * 1024;
        return freeMemoryMB;
    }

    @Override
    public int getTotalMemory() {
        ArrayList<String> memInfo = readFile();
        int memKB = 0;
        for (String line : memInfo){
            if (line.contains("MemTotal:")){
                memKB = parseMemLine(line);
            }
        }
        totalMemoryMB = memKB * 1024;
        return totalMemoryMB;
    }

    private ArrayList<String> readFile(){
        File f = new File(MEM_LOC);
        ArrayList<String> lines = new ArrayList<>();

        try (BufferedReader b = new BufferedReader(new FileReader(f))){
            String currentLine;
            while ((currentLine = b.readLine()) != null){
                lines.add(currentLine);
            }
        } catch (IOException e) {
            Log.error("File at location " + MEM_LOC + " does not exist");
            Log.error(e.toString());
        }
        return lines;
    }

    private Integer parseMemLine(String line) {
        boolean colonFound = false;
        boolean intFound = false;
        StringBuilder mem = new StringBuilder();
        for (char current : line.toCharArray()){
            if ((intFound) && (current == ' ')) break;
            if ((colonFound) && (current != ' ')){
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

class MemoryUsageWindows implements MemoryUsageInterface{
    @Override
    public int getUsedMemory() {
        return 0;
    }

    @Override
    public int getFreeMemory() {
        return 0;
    }

    @Override
    public int getTotalMemory() {
        return 0;
    }
}
