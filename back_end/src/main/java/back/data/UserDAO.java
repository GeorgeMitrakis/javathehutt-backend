package back.data;

import ys09.model.User;

import java.util.List;
import java.util.Optional;

public interface UserDAO {

    List<User> getUsers(Limits limits);

    Optional<User> getById(long id);

}
