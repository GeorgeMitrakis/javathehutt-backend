package back.model;

import java.util.LinkedHashMap;


public class User {

    private long id;
    private final String email;
    private final String role;
    private boolean isBanned;

    public User(long id, String email, String role) {
        this.id = id;
        this.email = email;
        this.role = role;
        isBanned = false;
    }

    public User(long id, String email, String role, boolean isBanned) {
        this.id = id;
        this.email = email;
        this.role = role;
        this.isBanned = isBanned;
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

    public boolean isBanned() {
        return isBanned;
    }

}
