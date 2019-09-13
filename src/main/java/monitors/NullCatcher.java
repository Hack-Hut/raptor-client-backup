package monitors;

import utils.Log;

import java.io.*;
import java.util.ArrayList;

import static utils.Exec.executeCommandGetOutput;

/**
 * This class gains some use full debugging information to analysts.
 * By swapping the special character file /dev/null with an ordinary
 * text file, if the build is designed in a way to send stuff to /dev/null,
 * making auditing harder, then this class will catch it.
 *
 * Note, that if a build is stopped half way /dev/null will still be a text
 * file, the back up of /dev/null will be located at /dev/null.bk
 */
public class NullCatcher implements MonitorInterface {
    private static final String ORIG_LOCATION = "/dev/null";
    private static final String BACKUP_LOCATION = ORIG_LOCATION + ".bk";
    private static final String CWD = new File("").getAbsolutePath();
    private static final String LOG_LOCATION = CWD + "/logs/null-catcher.log";
    private static String SEARCH_TERM = "RAPTOR TEST";


    public boolean setup(){
        try {
            File file = new File(LOG_LOCATION);
            file.createNewFile();
        } catch(Exception e) {
            Log.error("Failed to create log file at " + LOG_LOCATION);
            Log.error(e.toString());
            return false;
        }
        return true;
    }

    /**
     * If the file /dev/null is a special file, then swap it with a text file
     * Then write a some test text to see if we can read it.
     * @return if the reading of /dev/null worked.
     */
    public boolean start(){
        // Check to see if /dev/null is the real one or the text one.
        Log.debug("Attempting to start " + ORIG_LOCATION);

        if (isDevNullSpecialChar("/dev/null")){
            Log.debug("Replacing the original " + ORIG_LOCATION + " file.");
            if (!createNewDevNull()){
                Log.error("Failed to create /dev/null file.");
                return false;
            }
        }

        if (!clearDevNull()){
            return false;
        }

        if (!testDevNullCatcher()) {
            Log.error("Read from " + ORIG_LOCATION + " but did not find the test text.");
            return false;
        }
        return true;
    }

    public boolean stop(){
        Log.info("Replacing " + ORIG_LOCATION + " with the original");
        String[] cmd = {"mv", ORIG_LOCATION, LOG_LOCATION};
        utils.Exec.executeCommandGetOutput(cmd);
        utils.FileOperations.mv(BACKUP_LOCATION, ORIG_LOCATION);
        return true;
    }

    private boolean isDevNullSpecialChar(String path){
        final String[] cmd = {"file", path};
        for (String line: executeCommandGetOutput(cmd)) {
            if (line.contains("character special")) return true;
        }
        return false;
    }

    private boolean createNewDevNull(){
        Log.debug("Creating new " + ORIG_LOCATION +" file");
        if (utils.FileOperations.exists(BACKUP_LOCATION)){
            if (isDevNullSpecialChar(BACKUP_LOCATION)) {
                Log.warn("Cannot back up " + ORIG_LOCATION + " because there is already a file there at " + BACKUP_LOCATION);
                return false;
            }
        }
        if (!utils.FileOperations.mv(ORIG_LOCATION, BACKUP_LOCATION)){
            Log.error("Could not backup original " + ORIG_LOCATION);
            return false;
        }
        if (isDevNullSpecialChar(ORIG_LOCATION)){
        Log.error("Failed to move " + ORIG_LOCATION);
        }
        if (!utils.FileOperations.createNewFile(ORIG_LOCATION)) {
            Log.error("Failed to create empty file at " + ORIG_LOCATION);
            return false;
        }
        return true;
    }

    private boolean clearDevNull(){
        return utils.FileOperations.clearFile(ORIG_LOCATION);
    }

    private boolean testDevNullCatcher() {
        try {
            if (tryTestWriteToDevNull()) {
                return testReadFromDevNull();
            }
        }catch(IOException e){
            Log.error("Testing proxied /dev/null file failed.");
        }
        return false;
    }

    private boolean tryTestWriteToDevNull() throws IOException{
        ArrayList<String> lines = new ArrayList<>();
        lines.add(SEARCH_TERM);
        try {
            utils.FileOperations.writeToFile(ORIG_LOCATION, lines);
            return true;
        } catch (IOException e) {
            Log.error("Test /dev/null failed");
            Log.error("Failed to write to /dev/null");
            Log.error(e.toString());
            throw new IOException();
        }
    }

    private boolean testReadFromDevNull() throws IOException{
        File file = new File(ORIG_LOCATION);
        try (FileInputStream fis = new FileInputStream(file)) {
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);

            String line;
            while((line = br.readLine()) != null){
                if (line.contains(SEARCH_TERM)) {
                    br.close();
                    return true;
                }
            }
            br.close();
            return false;
        } catch (IOException e) {
            Log.error("Failed to read from " + ORIG_LOCATION);
            Log.error(e.toString());
            throw new IOException();
        }
    }

}
