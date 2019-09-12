package auditme.procmon;

import auditme.auditorParserInterface;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This class is used to Process the Process-ResourceMonitorOld CSV's
 * @author luke
 */
public class Procmon implements auditorParserInterface {

    private String procmonLoc;
    private Set<String> uniqueExes;
    private List<HashMap> buildExecutableInformation;


    /**
     * @param location of the procmon.csv file
     */
    private Procmon(String location) {
        this.procmonLoc = location;
    }

    @Override
    public boolean generateConfigurationFiles() {
        // TODO
        return false;
    }

    @Override
    public void generateExecutableFileList() {
        // TODO
    }

    /**
     * Parses the file to find unique executables
     * @return If the operation was successful
     * @throws FileNotFoundException if ProcessMonitor log file does not exist
     */
    public List<String> parse() throws FileNotFoundException {
        try {
            Set<String> unqiueExes = this.findUniqueExes();
            return utils.misc.convertSetToList(unqiueExes);
        }catch (FileNotFoundException e){
            throw new FileNotFoundException("The path " + this.procmonLoc + " does not have read permissions.");

        }
    }

    /**
     * This method will go through the unique executables and then find their,
     * md5, sha256, Scar plugin and determine if they need proxying.
     *
     * WARNING, will throw NullPointerException, if parse is not called
     * first
     */
    private void populateFileInformation() throws NullPointerException {
        for(String currentFile : this.uniqueExes){
            auditme.FileAttributes fileInfo = new auditme.FileAttributes(currentFile);
            fileInfo.populateFileInfo();
            fileInfo.showFileInfo();
            buildExecutableInformation.add(fileInfo.getInfo());
        }
    }

    private Set<String> findUniqueExes() throws FileNotFoundException{
        this.uniqueExes = new HashSet<>();
        List<String> fileLines = utils.FileOperations.readFileInToList(this.procmonLoc);
        for(String currentLine: fileLines){
            String exe = parseLine(currentLine);
            this.uniqueExes.add(exe);
        }
        return uniqueExes;
    }

    private String parseLine(String line) {
        int commaCount = 0;
        String executable = "";
        for(int x=0; x < line.length(); x++) {
            char currentChar = line.charAt(x);
            //CSV , split for path
            int splitLocation = 4;
            if (currentChar == ',') {
                commaCount++;
                if(commaCount > splitLocation) {
                    break;
                }
                continue;
            }
            if ((commaCount == splitLocation) && (currentChar != '"')) {
                executable += currentChar;
            }
        }
        return executable;
    }
}
