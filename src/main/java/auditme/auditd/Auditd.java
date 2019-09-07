package auditme.auditd;

import auditme.BuildInfoParser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author luke.goddard
 * This class is used to handle Auditd log files.
 */
public class Auditd implements BuildInfoParser {

    private static String AUDITD_EXECVE_SEARCH_VALUE = "type=EXECVE";
    private String auditdLocation;
    private List<String> uniqueExes;
    public List<HashMap> buildExecutableInformation;

    public Auditd(String auditdLocation) {
        this.auditdLocation = auditdLocation;
    }

    /**
     * Parses the Auditd log and then returns a unique list of executables that were found,
     * Note that this list contains the absolute paths of symbolic links.
     * @return Unique list of executables
     * @throws FileNotFoundException: If the monitors.auditd log location is not found.
     */
    public List<String> parse() throws FileNotFoundException  {
        try {
            Set<String> unqiueExes = this.findUniqueExecutables();
            this.uniqueExes = utils.misc.convertSetToList(unqiueExes);
            return this.uniqueExes;
        }
        catch(FileNotFoundException e) {
            throw new FileNotFoundException("The path " + this.auditdLocation + " does not have read permissions.");
        }
    }

    /**
     * This method will go through the unique executables and then find their,
     * md5, sha256, Scar plugin and determine if they need proxying.
     *
     * WARNING, will throw NullPointerException, if parse is not called
     * first
     * @return A list containing HashMaps for each file.
     * @throws NullPointerException
     */
    private List<HashMap> populateFileInformation() throws NullPointerException {
        for(String currentFile : this.uniqueExes){
            auditme.FileAttributes fileInfo = new auditme.FileAttributes(currentFile);
            fileInfo.populateFileInfo();
            fileInfo.showFileInfo();
            buildExecutableInformation.add(fileInfo.getInfo());
        }
        return buildExecutableInformation;
    }

    private Set<String> findUniqueExecutables() throws FileNotFoundException{
        File auditLog = new File(this.auditdLocation);
        BufferedReader buffer = new BufferedReader(new FileReader(auditLog));
        String currentLine;
        System.out.println("Reading " + this.auditdLocation);
        Set<String> uniqueExecutables = new HashSet<>();
        try {
            if(!auditLog.canRead()) {
                buffer.close();
                throw new FileNotFoundException("The path " + this.auditdLocation + " does not have read permissions.");
            }
            while ((currentLine = buffer.readLine()) != null) {
                if(currentLine.contains(AUDITD_EXECVE_SEARCH_VALUE)) {
                    uniqueExecutables.add(parseExecutableLine(currentLine));
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return uniqueExecutables;
    }

    /**
     * Parses an Auditd line to extract the binaries that were executed.
     * @param auditdLine: line from the monitors.auditd log
     * @return the absolute path to the executable, or if it's a sym then it's target.
     * @throws IOException: if the file executable path cannot be converted to canonical path
     */
    private static String parseExecutableLine(String auditdLine) throws IOException {
        boolean tokenMode = false;
        StringBuilder executablePath = new StringBuilder();
        for (int currentChar = 0; currentChar < auditdLine.length(); currentChar++) {
            if (auditdLine.charAt(currentChar) == '"' && !tokenMode) {
                tokenMode = true;
            } else if (auditdLine.charAt(currentChar) == '"' && tokenMode) {
                break;
            } else if (currentChar != '"' && tokenMode) {
                executablePath.append(auditdLine.charAt(currentChar));
            }
        }
        return cleanLine(executablePath.toString());
    }

    private static String cleanLine(String executable) throws IOException{
        File f = new File(executable);
        try {
            String realPath =  f.getCanonicalPath();
            String symTarget = utils.FileOperations.getSymLocation(realPath);
            if (symTarget.equals("")) {
                return realPath;
            }
            return symTarget;
        }
        catch (IOException e) {
            throw new IOException();
        }
    }

    public static void main(String args[]) {
        String path = "/home/audit.log";
        Auditd auditd = new Auditd(path);
        try{
            auditd.parse();
            auditd.populateFileInformation();
        }
        catch(FileNotFoundException e){
            System.out.println("The file " + path + " does not exist, or cannot be read.");
        }
    }
}