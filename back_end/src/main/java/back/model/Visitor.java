package back.model;

import back.api.JTHInputException;


public class Visitor extends User{

    public Visitor(User usr /* TODO: add extras */){
        super(usr.getId(), usr.getEmail(), usr.getName(), usr.getSurname(), "visitor");
    }

    public Visitor(long id, String email, String name, String surname /* TODO: add extras */){
        super(id, email, name, surname, "visitor");
    }

}
