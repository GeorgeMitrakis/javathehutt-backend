package back.data.jdbc;

import back.model.Provider;
import back.model.User;
import back.model.Visitor;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;


class VisitorRowMapper implements RowMapper<Visitor>  {

    private User user = null;

    public VisitorRowMapper(User _user){
        super();
        user = _user;
    }

    @Override
    public Visitor mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new Visitor(user);
    }
}

