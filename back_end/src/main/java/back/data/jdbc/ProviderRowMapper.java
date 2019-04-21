package back.data.jdbc;

import back.model.Provider;
import back.model.User;
import back.model.Visitor;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ProviderRowMapper implements RowMapper<Provider> {


    @Override
    public Provider mapRow(ResultSet rs, int rowNum) throws SQLException {

            return new Provider(
                    rs.getLong("id"),
                    rs.getString("email"),
                    rs.getString("name"),
                    rs.getString("surname")
            );

    }
}
