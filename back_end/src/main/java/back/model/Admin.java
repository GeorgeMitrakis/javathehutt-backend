package back.model;

import java.util.LinkedHashMap;

public class Admin extends User {

    private final String name;
    private final String surname;

    public Admin(User usr, String _name, String _surname){
        super(usr.getId(), usr.getEmail(), "admin");
        name = _name;
        surname = _surname;
    }

    public Admin(long id, String email, String _name, String _surname){
        super(id, email, "admin");
        name = _name;
        surname = _surname;
    }

    public Admin(LinkedHashMap M) {
        super(
                (Integer) M.get("id"),
                (String) M.get("email"),
                (String) M.get("role")
        );
        this.name = (String)M.get("name");
        this.surname = (String)M.get("surname");
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }
}
