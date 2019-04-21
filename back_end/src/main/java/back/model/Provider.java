package back.model;

import back.api.JTHInputException;


public class Provider extends User{

    public Visitor(User usr /* TODO: add extras */) throws JTHInputException{
        super(usr.getId(), usr.getEmail(), usr.getName(), usr.getSurname(), "provider");
    }

    public Visitor(long id, String email, String name, String surname /* TODO: add extras */){
        super(id, email, name, surname, "provider");
    }

}
