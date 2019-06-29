package back.model;


import back.exceptions.JTHAuthException;

import java.util.LinkedHashMap;


public class User {

    private long id;
    private final String email;
    private final String role;

    public User(long id, String email, String role) {
        this.id = id;
        this.email = email;
        this.role = role;
    }

    public static User fromLinkedHashMap(LinkedHashMap M){
        String role = (String)(M.get("role"));
        switch (role){
            case "visitor":
                return new Visitor(M);
            case "provider":
                return new Provider(M);
            case "admin":
                return new Admin(M);
            default:
                return null;

        }
    }

    public long getId() {
        return id;
    }

    public void setId(long id) { this.id = id; }

    public String getEmail() { return email; }

    public String getRole() { return role;}

    public void isAdmin() throws JTHAuthException {
        if(!this.role.equals("admin")){
            //TODO: always succeed for now because front end is not implemented
            // throw new JTHAuthException();
        }
    }
}
