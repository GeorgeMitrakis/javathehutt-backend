package back.data.jdbc;

import back.model.Provider;
import back.model.User;
import back.model.Visitor;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UltimateUserRowMapper implements RowMapper<User> {

    @Override
    public User mapRow(ResultSet rs, int rowNum) throws SQLException {
        long id = rs.getLong("id");
        String email = rs.getString("email");
        String role = rs.getString("role");
        boolean isBanned = rs.getBoolean("isbanned");
        String name = null, surname = null, providerName = null;
        User u = new User(id, email, role, isBanned);
        switch(role){
            case "visitor":
                name = rs.getString("name");
                surname = rs.getString("surname");
                return new Visitor(u, name, surname);
            case "provider":
                providerName = rs.getString("providerName");
                return new Provider(u, providerName);
            default:
                return u;
        }
    }

}
