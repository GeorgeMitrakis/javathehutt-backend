package back.model;

import back.api.JTHInputException;


public class Provider extends User{

    public Provider(User usr /* TODO: add extras */){
        super(usr.getId(), usr.getEmail(), usr.getName(), usr.getSurname(), "provider");
    }

    public Provider(long id, String email, String name, String surname /* TODO: add extras */){
        super(id, email, name, surname, "provider");

    }

}
