package back.data;

import back.data.jdbc.DataAccess;
import back.exceptions.JTHAuthException;
import back.exceptions.JTHDataBaseException;
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
    public long countUsers() throws JTHDataBaseException {
        return dataAccess.countUsers();
    }

    @Override
    public boolean getUserBan(long id) throws JTHDataBaseException {
        return dataAccess.getUserBan(id);
    }

    @Override
    public void setUserBan(User user, boolean ban) throws JTHDataBaseException {
        dataAccess.setUserBan(user, ban);
    }

    @Override
    public boolean promoteUserToAdmin(User user) throws JTHDataBaseException {
        return dataAccess.promoteUser(user);
    }

    @Override
    public boolean deleteUser(User user) throws JTHDataBaseException {
        return dataAccess.deleteUser(user);
    }

    @Override
    public List<User> getUsers(Limits limits) throws JTHDataBaseException {
        List<User> users = dataAccess.getUsers(limits.getStart(), limits.getCount());
        limits.setTotal(dataAccess.countUsers());
        return users;
    }

    @Override
    public User getById(long id) throws JTHDataBaseException {
        return dataAccess.getUser(id);
    }

    @Override
    public User getByEmail(String email) throws JTHDataBaseException {
        return dataAccess.getUser(email);
    }

    @Override
    public List<User> getUsersByEmailPrefix(String emailPrefix) throws JTHDataBaseException {
        return dataAccess.getUsersByEmailPrefix(emailPrefix);
    }

    @Override
    public User getByCredentials(String email, String hashedPassword) throws JTHAuthException, JTHDataBaseException {
        if ( dataAccess.getUser(email) != null ){                 // if email exists..
            User u = dataAccess.getUser(email, hashedPassword);
            if (u == null) throw new JTHAuthException();          // ..but password is wrong then AUTH exception
            return u;
        } else return null;
    }

    @Override
    public boolean updateUserInfo(long userId, String newemail, String newpassword, String oldpassword, String newname, String newsurname, String newProviderName) throws JTHAuthException, JTHDataBaseException {
        if (newpassword != null && oldpassword != null && !dataAccess.checkAuthentication(userId, oldpassword)) {
            throw new JTHAuthException();
        }
        dataAccess.updateBasicUserInfo(userId, newemail, newpassword);
        User user = dataAccess.getUser(userId);
        if (user != null && user.getRole().equals("visitor") && ((newname != null && !newname.equals("")) || (newsurname != null && !newsurname.equals("")))){
            dataAccess.updateVisitor(userId, newname, newsurname);
        } else if (user != null && user.getRole().equals("provider") && newProviderName != null && !newProviderName.equals("")) {
            dataAccess.updateProvider(userId, newProviderName);
        } else if (user == null){
            return false;
        }
        return true;
    }

    @Override
    public void storeUser(Provider p, String password) throws JTHDataBaseException {
        dataAccess.storeUser(p, password);
    }

    @Override
    public void storeUser(Visitor v, String password) throws JTHDataBaseException {
        dataAccess.storeUser(v, password);
    }

}
