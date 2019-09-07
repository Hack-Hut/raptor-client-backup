package auditme;

import java.io.FileNotFoundException;
import java.util.List;

public interface BuildInfoParser {
    List<String> parse() throws FileNotFoundException;
}
