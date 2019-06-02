package back.model;

import java.util.LinkedHashMap;
import java.util.List;


public class Visitor extends User{

    private final String name;
    private final String surname;
    private List<Room> favouriteRooms = null;

    public Visitor(User usr, String _name, String _surname){
        super(usr.getId(), usr.getEmail(), "visitor");
        name = _name;
        surname = _surname;
    }

    public Visitor(long id, String email, String _name, String _surname){
        super(id, email, "visitor");
        name = _name;
        surname = _surname;
    }

    public Visitor(LinkedHashMap M){
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

    public List<Room> fetchFavouriteRooms(){
        if (favouriteRooms != null){
            // TODO: fetch favourite rooms from DB
            favouriteRooms = /* TODO */ null;
        }
        return favouriteRooms;
    }

}
