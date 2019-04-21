package back.data.jdbc;

import back.data.JTHAuthException;
import back.data.Limits;
import back.data.UserDAO;
import back.model.Provider;
import back.model.User;
import back.model.Visitor;

import java.util.List;
import java.util.Optional;

public class UserDAOImpl implements UserDAO {

    private final DataAccess dataAccess;

    public UserDAOImpl(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    @Override
    public List<User> getUsers(Limits limits) {
        List<User> users = dataAccess.getUsers(limits.getStart(), limits.getCount());
        limits.setTotal(dataAccess.countUsers());
        return users;
    }

    @Override
    public Optional<User> getById(long id) {
        return dataAccess.getUser(id);
    }

    @Override
    public Optional<User> getByCredentials(String email, String hashedPassword) throws JTHAuthException {
        return dataAccess.getUser(email, hashedPassword);
    }

    @Override
    public void storeUser(Provider p, String password) {
        dataAccess.storeUser(p,password);
    }

    @Override
    public void storeUser(Visitor v, String password) {
        dataAccess.storeUser(v,password);
    }
}
