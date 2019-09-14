package monitors;

public interface MonitorInterface {
    boolean setup();
    boolean start();
    boolean stop();
    boolean test();
}
