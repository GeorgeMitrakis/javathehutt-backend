package back.model;

import java.util.LinkedHashMap;


public class Provider extends User{

    private final String providername;

    public Provider(User usr, String _providername){
        super(usr.getId(), usr.getEmail(), "provider", usr.isBanned());
        providername = _providername;
    }

    public Provider(long id, String email, String _providername){
        super(id, email,"provider");
        providername = _providername;
    }

    public Provider(LinkedHashMap M){
        super(
                (Integer) M.get("id"),
                (String) M.get("email"),
                (String) M.get("role")
        );
        this.providername = (String)M.get("providername");
    }

    public String getProvidername() {
        return providername;
    }

}
