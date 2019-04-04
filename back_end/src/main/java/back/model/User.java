package back.model;

public class User {

    private final long id;
    private final String email;
    private final String name;
    private final String surname;

    public User(long id, String email, String name, String surname) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.surname = surname;
    }

    public long getId() {
        return id;
    }

    public String getEmail() { return email; }

    public String getName() {
        return name;
    }

    public String getSurname() { return surname; }
}
