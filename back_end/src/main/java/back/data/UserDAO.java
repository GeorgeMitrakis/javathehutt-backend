package back.data;

import back.exceptions.JTHAuthException;
import back.exceptions.JTHDataBaseException;
import back.model.Provider;
import back.model.User;
import back.model.Visitor;

import java.util.List;


public interface UserDAO {

    long countUsers() throws JTHDataBaseException;

    List<User> getUsers(Limits limits) throws JTHDataBaseException;

    User getById(long id) throws JTHDataBaseException;

    User getByEmail(String email) throws JTHDataBaseException;

    List<User> getUsersByEmailPrefix(String emailPrefix) throws JTHDataBaseException;

    User getByCredentials(String email, String hashedPassword) throws JTHAuthException, JTHDataBaseException;

    void storeUser(Provider p, String password) throws JTHDataBaseException;

    void storeUser(Visitor v, String password) throws JTHDataBaseException;

    boolean getUserBan(long id) throws JTHDataBaseException;

    void setUserBan(User user, boolean ban) throws JTHDataBaseException;

    boolean promoteUserToAdmin(User user) throws JTHDataBaseException;

    boolean deleteUser(User user) throws JTHDataBaseException;

}
