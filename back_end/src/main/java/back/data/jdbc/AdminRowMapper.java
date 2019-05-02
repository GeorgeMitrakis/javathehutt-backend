package back.data.jdbc;

import back.model.Admin;
import back.model.User;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class AdminRowMapper implements RowMapper<Admin> {

    private User user = null;

    public AdminRowMapper(User _user){
        super();
        user = _user;
    }

    @Override
    public Admin mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new Admin(user, rs.getString("name"), rs.getString("surname"));
    }

}
