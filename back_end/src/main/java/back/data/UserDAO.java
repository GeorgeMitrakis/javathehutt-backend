package back.data;

import back.model.Provider;
import back.model.User;
import back.model.Visitor;

import java.util.List;
import java.util.Optional;

public interface UserDAO {

    List<User> getUsers(Limits limits);

    Optional<User> getById(long id);

    Optional<User> getByCredentials(String email, String hashedPassword) throws JTHAuthException;

    void storeUser(Provider p,String password);

    void storeUser(Visitor v,String password);
}
