package back.data;

import back.Exceptions.JTHAuthException;
import back.Exceptions.JTHDataBaseException;
import back.model.Provider;
import back.model.User;
import back.model.Visitor;

import java.util.List;


public interface UserDAO {

    long countUsers() throws JTHDataBaseException;

    List<User> getUsers(Limits limits) throws JTHDataBaseException;

    User getById(long id) throws JTHDataBaseException;

    List<User> getUsersByEmail(String email) throws JTHDataBaseException;

    User getByCredentials(String email, String hashedPassword) throws JTHAuthException, JTHDataBaseException;

    void storeUser(Provider p, String password) throws JTHDataBaseException;

    void storeUser(Visitor v, String password) throws JTHDataBaseException;

    boolean getUserBan(long id) throws JTHDataBaseException;

    void setUserBan(User user, boolean ban) throws JTHDataBaseException;

    boolean promoteUserToAdmin(User user) throws JTHDataBaseException;

    boolean deleteUser(User user) throws JTHDataBaseException;

}
