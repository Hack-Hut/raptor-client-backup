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
    private int SplitLocation = 4; //CSV , split for path
    public List<HashMap> buildExecutableInformation;


    /**
     * @param location of the procmon.csv file
     */
    public Procmon(String location) {
        this.procmonLoc = location;
    }

    /**
     * Parses the file to find unique executables
     * @return If the operation was successful
     * @throws FileNotFoundException
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
     * Goes through the list of executables and prints them
     * @param executables List executables that were parsed
     */
    public static void prettyPrintExecutables(List<String> executables) {
        for(String exe: executables){
            System.out.println(exe);
        }
    }

    /**
     * This method will go through the unique executables and then find their,
     * md5, sha256, Scar plugin and determine if they need proxying.
     *
     * WARNING, will throw NullPointerException, if parse is not called
     * first
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

    /**
     * Finds unique executes in the file procmonLoc
     * @return A list of unique executes that were found in the file
     * @throws FileNotFoundException
     */
    private Set<String> findUniqueExes() throws FileNotFoundException{
        this.uniqueExes = new HashSet<>();
        List<String> fileLines = utils.FileOperations.readFileInToList(this.procmonLoc);
        for(String currentLine: fileLines){
            String exe = parseLine(currentLine);

            // TODO: Remove this line once finished writing the Procmon class
            //if (common.FileOperators.exists(exe)) {
            this.uniqueExes.add(exe);
            //}
        }
        return uniqueExes;
    }

    /**
     * Parses an individual line from procmon.csv
     * @param line  Line from the file
     * @return the executable cell from the csv line.
     */
    private String parseLine(String line) {
        int commaCount = 0;
        String executable = "";
        for(int x=0; x < line.length(); x++) {
            char currentChar = line.charAt(x);
            if (currentChar == ',') {
                commaCount++;
                if(commaCount > this.SplitLocation) {
                    break;
                }
                continue;
            }
            if ((commaCount == this.SplitLocation) && (currentChar != '"')) {
                executable += currentChar;
            }
        }
        return executable;
    }

    public static void main(String args[]) {
        Procmon procmon = new Procmon("/home/luke/eclipse-workspace/raptor-bundle/src/unitTestData/auditme/procmon.csv");
        try {
            procmon.parse();
            procmon.populateFileInformation();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean generateConfigurationFiles() {
        // TODO
        return false;
    }
}
