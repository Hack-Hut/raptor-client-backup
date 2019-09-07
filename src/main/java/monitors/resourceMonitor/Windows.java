package monitors.resourceMonitor;

public class Windows implements ResourceInterface {
    public Integer getFreeMemKB(){
        return 0;
    }
    public Integer getTotalMemKB(){
        return 0;
    }
    public Double getCPUUsagePercentage(){
        return Double.NaN;
    }
}
