public class CallException extends Exception {

    private String msg;
    
    CallException(String message) {
        super(message);
        msg = message;
    }
    
    public String getMessage() {
        return msg;
    }
    
    public String toString() {
        return msg;
    }
    

}