package back.data.jdbc;

import org.springframework.jdbc.core.RowMapper;
import back.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;

class UserRowMapper implements RowMapper<User>  {

	@Override
    public User mapRow(ResultSet rs, int rowNum) throws SQLException {
        
        long id = rs.getLong("id");
        String email = rs.getString("email");
        String name = rs.getString("name");
        String surname = rs.getString("surname");
        String hashedPassword = rs.getString("hashedPassword");
        return new User(id, email, name, surname);
    }
}