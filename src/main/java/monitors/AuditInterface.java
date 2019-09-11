package monitors;

public interface AuditInterface {
    Object[] getExecutables();
    boolean start();
    boolean stop();
}
