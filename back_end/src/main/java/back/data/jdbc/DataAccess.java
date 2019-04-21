package back.data.jdbc;

import back.data.JTHSecurity;
import back.model.Provider;
import back.model.Visitor;
import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import back.data.Limits;
import back.model.User;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DataAccess {

    private static final int MAX_TOTAL_CONNECTIONS = 16;
    private static final int MAX_IDLE_CONNECTIONS  = 8;
    
    private DataSource dataSource;
    private JdbcTemplate jdbcTemplate;

    public void setup(String driverClass, String url, String user, String pass) throws SQLException {

        //initialize the data source
	    BasicDataSource bds = new BasicDataSource();
        bds.setDriverClassName(driverClass);
        bds.setUrl(url);
        bds.setMaxTotal(MAX_TOTAL_CONNECTIONS);
        bds.setMaxIdle(MAX_IDLE_CONNECTIONS);
        bds.setUsername(user);
        bds.setPassword(pass);
        bds.setValidationQuery("SELECT 1");
        bds.setTestOnBorrow(true);
        bds.setDefaultAutoCommit(true);

        //check that everything works OK
        bds.getConnection().close();

        //initialize the jdbc template utility
        jdbcTemplate = new JdbcTemplate(bds);

        //keep the dataSource for the low-level manual example to function (not actually required)
        dataSource = bds;
    }

    public long countUsers() {
        return jdbcTemplate.queryForObject("select count(*) from user", Long.class);
    }

    public List<User> getUsers(long start, long count) {
        Long[] params = new Long[]{count, start};
        return jdbcTemplate.query("select * from \"user\" limit ? offset ?", params, new UserRowMapper());
    }

    public Optional<User> getUser(Long id) {
        try {
            Long[] par = new Long[]{id};
            User u = jdbcTemplate.queryForObject("select * from \"user\" where id = ?", par, new UserRowMapper());
            if (u == null) return Optional.empty();
            switch (u.getRole()) {
                case "visitor":
                    Visitor v = jdbcTemplate.queryForObject("SELECT * FROM visitor WHERE id = ?", par, new VisitorRowMapper(u));
                    return (v == null) ? Optional.of(v) : Optional.empty();
                case "provider":
                    Provider p = jdbcTemplate.queryForObject("SELECT * FROM provider WHERE id = ?", par, new ProviderRowMapper(u));
                    return (p == null) ? Optional.of(p) : Optional.empty();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Optional<User> getUser(String email, String hashedPassword) {
        try {
            String[] params = new String[]{email, hashedPassword};
            User u = jdbcTemplate.queryForObject("select * from \"user\" where email = ? and password = ?", params, new UserRowMapper());
            if (u == null) return Optional.empty();
            Long[] par = new Long[]{u.getId()};
            switch (u.getRole()) {
                case "visitor":
                    Visitor v = jdbcTemplate.queryForObject("SELECT * FROM visitor WHERE id = ?", par, new VisitorRowMapper(u));
                    return (v == null) ? Optional.of(v) : Optional.empty();
                case "provider":
                    Provider p = jdbcTemplate.queryForObject("SELECT * FROM provider WHERE id = ?", par, new ProviderRowMapper(u));
                    return (p == null) ? Optional.of(p) : Optional.empty();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public void storeUser(Provider p, String hashedPassword) {
        try {
            jdbcTemplate.update("INSERT INTO \"user\"(email, name, surname, password, role) VALUES (?,?,?,?,?)",
                                 p.getEmail(), p.getName(), p.getSurname(), hashedPassword, "provider");
            jdbcTemplate.update("INSERT INTO provider (id) VALUES (?)", p.getId());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void storeUser(Visitor v, String hashedPassword) {
        try {
            jdbcTemplate.update("INSERT INTO \"user\"(email, name, surname, password, role) VALUES (?,?,?,?,?)",
                                v.getEmail(), v.getName(), v.getSurname(), hashedPassword, "provider");
            jdbcTemplate.update("INSERT INTO visitor (id) VALUES (?)", v.getId());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}