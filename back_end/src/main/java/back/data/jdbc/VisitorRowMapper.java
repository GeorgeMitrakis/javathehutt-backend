package back.data.jdbc;

import back.model.Visitor;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;


public class VisitorRowMapper implements RowMapper<Visitor> {

    @Override
    public Visitor mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new Visitor(
            rs.getLong("id"),
            rs.getString("email"),
            rs.getString("name"),
            rs.getString("surname")
        );
    }
}
