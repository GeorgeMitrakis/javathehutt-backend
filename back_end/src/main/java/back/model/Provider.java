package back.model;

import back.api.JTHInputException;


public class Provider extends User{

    private final String providername;

    public Provider(User usr, String _providername){
        super(usr.getId(), usr.getEmail(), "provider");
        providername = _providername;
    }

    public Provider(long id, String email, String _providername){
        super(id, email,"provider");
        providername = _providername;
    }

    public String getProvidername() {
        return providername;
    }

}
