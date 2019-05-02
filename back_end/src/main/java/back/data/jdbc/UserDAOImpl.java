package back.data.jdbc;

import back.data.JTHAuthException;
import back.data.Limits;
import back.data.UserDAO;
import back.model.Provider;
import back.model.User;
import back.model.Visitor;

import java.util.List;


public class UserDAOImpl implements UserDAO {

    private final DataAccess dataAccess;

    public UserDAOImpl(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    @Override
    public boolean setUserBan(User user, boolean ban){
        return dataAccess.setUserBan(user, ban);
    }

    @Override
    public boolean promoteUserToAdmin(User user){
        return dataAccess.promoteUser(user);
    }

    @Override
    public boolean deleteUser(User user){
        return dataAccess.deleteUser(user);
    }

    @Override
    public List<User> getUsers(Limits limits) {
        List<User> users = dataAccess.getUsers(limits.getStart(), limits.getCount());
        limits.setTotal(dataAccess.countUsers());
        return users;
    }

    @Override
    public User getById(long id) {
        return dataAccess.getUser(id);
    }

    @Override
    public User getByEmail(String email) {
        return dataAccess.getUser(email);
    }

    @Override
    public User getByCredentials(String email, String hashedPassword) throws JTHAuthException {
        if ( dataAccess.getUser(email) != null ){                 // if email exists..
            User u = dataAccess.getUser(email, hashedPassword);
            if (u == null) throw new JTHAuthException();          // ..but password is wrong then AUTH exception
            return u;
        } else return null;
    }

    @Override
    public boolean storeUser(Provider p, String password) {
        return dataAccess.storeUser(p, password);
    }

    @Override
    public boolean storeUser(Visitor v, String password) {
        return dataAccess.storeUser(v, password);
    }

}
