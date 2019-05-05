package back.data;

import back.model.Provider;
import back.model.User;
import back.model.Visitor;

import java.util.List;
import java.util.Optional;

public interface UserDAO {

    List<User> getUsers(Limits limits);

    User getById(long id);

    User getByEmail(String email);

    User getByCredentials(String email, String hashedPassword) throws JTHAuthException;

    boolean storeUser(Provider p, String password);

    boolean storeUser(Visitor v, String password);

    boolean getUserBan(long id);

    boolean setUserBan(User user, boolean ban);

    boolean promoteUserToAdmin(User user);

    boolean deleteUser(User user);

}
