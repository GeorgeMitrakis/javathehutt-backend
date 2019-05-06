package back.model;

import back.api.JTHInputException;

import java.util.LinkedHashMap;


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

    public Provider(LinkedHashMap M){
        super(
                ((Integer)M.get("id")).intValue(),
                (String) M.get("email"),
                (String) M.get("role")
        );
        this.providername = (String)M.get("providername");
    }

    public String getProvidername() {
        return providername;
    }

}
