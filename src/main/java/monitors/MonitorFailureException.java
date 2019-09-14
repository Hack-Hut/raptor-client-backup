package monitors;

public class MonitorFailureException extends Exception{
    public MonitorFailureException(){
        super();
    }
    public MonitorFailureException(String message){
        super(message);
    }
}
