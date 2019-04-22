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

    private User getUserByRole(String role, Object[] par, User u){
        switch (role) {
            case "visitor":
                Visitor v = jdbcTemplate.queryForObject("SELECT * FROM visitor WHERE id = ?", par, new VisitorRowMapper(u));
                return v;
            case "provider":
                Provider p = jdbcTemplate.queryForObject("SELECT * FROM provider WHERE id = ?", par, new ProviderRowMapper(u));
                return p;
            default:
                return null;
        }
    }

    public User getUser(Long id) {
        try {
            Long[] par = new Long[]{id};
            User u = jdbcTemplate.queryForObject("select * from \"user\" where id = ?", par, new UserRowMapper());
            return (u != null) ? getUserByRole(u.getRole(), par, u) : null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public User getUser(String email) {
        try {
            User u = jdbcTemplate.queryForObject("select * from \"user\" where email = ?", new String[]{email}, new UserRowMapper());
            return (u != null) ? getUserByRole(u.getRole(), new Long[]{u.getId()}, u) : null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public User getUser(String email, String hashedPassword) {
        try {
            User u = jdbcTemplate.queryForObject("select * from \"user\" where email = ? and password = ?", new String[]{email, hashedPassword}, new UserRowMapper());
            return (u != null) ? getUserByRole(u.getRole(), new Long[]{u.getId()}, u) : null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean storeUser(Provider p, String hashedPassword) {
        try {
            int id = jdbcTemplate.update("INSERT INTO \"user\"(email, password, role) VALUES (?, ?, ?) RETURNING id",
                                         p.getEmail(), hashedPassword, "provider");
            jdbcTemplate.update("INSERT INTO provider (id, providername) VALUES (?, ?)", id, p.getProvidername());
            p.setId(id);
        } catch (Exception e) {
            System.err.println("Failed to store provider user");
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean storeUser(Visitor v, String hashedPassword) {
        try {
            int id = jdbcTemplate.update("INSERT INTO \"user\"(email, password, role) VALUES (?, ?, ?) RETURNING id",
                                         v.getEmail(), hashedPassword, "visitor");
            jdbcTemplate.update("INSERT INTO visitor (\"name\", surnmae) VALUES (?, ?, ?)", id, v.getName(), v.getSurname());
            v.setId(id);
        } catch (Exception e) {
            System.err.println("Failed to store visitor user");
            e.printStackTrace();
            return false;
        }
        return true;
    }
}