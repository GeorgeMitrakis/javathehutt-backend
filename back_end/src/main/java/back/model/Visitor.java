package back.model;

import back.api.JTHInputException;
import org.restlet.data.Form;

public class Visitor extends User{

    public Visitor(Form form) throws JTHInputException{
        super(0,null,null,null, "visitor");
    }

    public Visitor(long id, String email, String name, String surname){
        super(id, email, name, surname, "visitor");
    }
    public static void validateForm(Form form) throws JTHInputException {
        throw new JTHInputException(); //incomplete
    }
}
