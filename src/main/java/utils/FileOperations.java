package utils;

import java.io.*;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class FileOperations {
    /**
     * Note that broken links return an empty String.
     * @param fileName Location of the file
     * @return the symbolic link location of the file
     */
    public static String getSymLocation(String fileName) {
        File f = new File(fileName);
        Path realPath;
        try {
            realPath = f.toPath().toRealPath();
        }
        catch (NoSuchFileException e){
            return "";
        }
        catch (IOException e) {
            Log.error(e.toString());
            return "";
        }
        return realPath.toString();
    }

    /**
     * Check to see if a file exists
     * @param path location of the file to test
     * @return true or false
     */
    public static boolean exists(String path) {
        File tempFile = new File(path);
        return tempFile.exists();
    }

    /**
     * Check to see if a file exists
     * @param path location of the file to test
     * @return true or false
     */
    public static boolean canRead(String path) {
        File tempFile = new File(path);
        return tempFile.canRead();
    }


    /**
     * Gets the base path for a file; for example
     * 		\dir1\dir2\someFile
     * would return
     * 		someFile
     * Note this is OS independent.
     * @param path: FileOperations path location
     * @return base path
     */
    public static String getBasePath(String path) {
        char pathSplit = File.separatorChar;
        StringBuilder currentChunk = new StringBuilder();
        for(int i = 0; i < path.length(); i++) {
            char currentChar = path.charAt(i);
            // ignore last char if its a slash
            if ((i == path.length() -1) && (currentChar == pathSplit)) {
                break;
            }
            else if (currentChar == pathSplit){
                currentChunk = new StringBuilder();
                continue;
            }
            currentChunk.append(currentChar);
        }
        return currentChunk.toString();
    }

    /**
     * Give a location to a file, this method will iterate through the lines
     * in the file and add them into a list
     * @param path: location of the file
     * @return List of lines in the file.
     * @throws FileNotFoundException
     */
    public static List<String> readFileInToList(String path) throws FileNotFoundException{
        List<String> fileLines = new ArrayList<>();
        String line;
        try(BufferedReader reader = new BufferedReader(new FileReader(path))) {
            line = reader.readLine();
            while (line != null) {
                fileLines.add(line);
                line = reader.readLine();
            }
        }
        catch (IOException e) {
            Log.error(e.toString());
        }
        return fileLines;
    }

    public static boolean createNewFile(String path){
        File file = new File(path);
        try {
            if(file.createNewFile()) return true;
        } catch (IOException e) {
            Log.error("Failed to create file at " + path);
            Log.error(e.toString());
        }
        return false;
    }

    public static boolean clearFile(String path) {
        try {
            PrintWriter writer = new PrintWriter(path);
            writer.print("");
            writer.close();
            return true;
        } catch (FileNotFoundException e) {
            Log.error("Failed to clear file at " + path + " because it does not exist");
        }
        return false;
    }

    public static boolean mv(String src, String dest){
        File srcFile = new File(src);
        File destFile = new File(dest);

        if (!srcFile.exists()) {
            Log.error("Trying to move file from " + src + " to " + dest + " but cannot because src does not exist.");
            return false;
        }

        if (destFile.exists()) Log.warn("Overwriting file at " + dest);
        if (srcFile.renameTo(new File(dest))) return true;
        Log.error("Failed trying to move file from " + src + " to " + dest + " for an unknown reason.");
        return false;
    }

    public static void writeToFile(String path, List lines) throws IOException {
        FileWriter fileWriter = new FileWriter(path);
        PrintWriter printWriter = new PrintWriter(fileWriter);
        for (Object line: lines) printWriter.print(line.toString());
        printWriter.close();
    }
}
