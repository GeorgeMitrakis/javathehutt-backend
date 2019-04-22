package back.api;

public class JTHInputException extends Throwable {

    final private String msg;

    public JTHInputException() {
        super();
        msg = "Unknown";
    }
    public JTHInputException(String _msg){
        super();
        msg = _msg;
    }

    public String getErrorMsg() { return msg; }

}
