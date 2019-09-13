package buildToolsConfigGenerator;

/**
 * Interface used by the different data source options.
 * This includes auditd, audisp-remote and process monitor
 */
public interface auditorParserInterface {
    boolean generateConfigurationFiles();
    void generateExecutableFileList();
}
