package back.model;

public class User {

    private final long id;
    private final String email;
    private final String role;

    public User(long id, String email, String role) {
        this.id = id;
        this.email = email;
        this.role = role;
    }

    public long getId() {
        return id;
    }

    public String getEmail() { return email; }

    public String getRole() { return role;}
}
