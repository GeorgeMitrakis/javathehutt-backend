package back.exceptions;

public class JTHInputException extends JTHException {

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
