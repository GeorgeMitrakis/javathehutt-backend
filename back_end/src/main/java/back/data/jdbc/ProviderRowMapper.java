package back.data.jdbc;

import back.model.Provider;
import back.model.User;
import back.model.Visitor;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;


class ProviderRowMapper implements RowMapper<Provider>  {

    private User user = null;

    public ProviderRowMapper(User _user){
        super();
        user = _user;
    }

    @Override
    public Provider mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new Provider(user, rs.getString("providername"));
    }
}
