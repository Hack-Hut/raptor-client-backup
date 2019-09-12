package monitors.bepStep;

import java.util.ArrayList;

public interface ProcessInterface {
    String getCwd();
    String getCmd();
    ArrayList <Integer> getChildren();
}
