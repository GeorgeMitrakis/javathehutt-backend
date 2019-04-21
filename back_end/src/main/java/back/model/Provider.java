package back.model;

import back.api.JTHInputException;
import org.restlet.data.Form;

public class Provider extends User{

    public Provider(Form form) throws  JTHInputException{
        super(0,null,null,null, "");
    }



    public Provider(long id, String email, String name, String surname) {
        super(id,email,name,surname,"provider");
    }

    public static void validateForm(Form form) throws JTHInputException {
        throw new JTHInputException(); //incomplete
    }


}
