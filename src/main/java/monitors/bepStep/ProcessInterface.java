package monitors.bepStep;

import java.util.ArrayList;

public interface ProcessInterface {
    String getEnviron();
    String getCwd();
    String getCmd();
    ArrayList <String> getDescriptors();
    ArrayList <Integer> getChildren();
}
