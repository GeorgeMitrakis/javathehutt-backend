package back.data.jdbc;

import back.data.JTHSecurity;
import back.model.Provider;
import back.model.Visitor;
import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import back.data.Limits;
import back.model.User;
import org.springframework.jdbc.support.KeyHolder;

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
        } catch (EmptyResultDataAccessException e){
            return null;
        } catch (IncorrectResultSizeDataAccessException e){
            return null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public User getUser(String email) {
        try {
            User u = jdbcTemplate.queryForObject("select * from \"user\" where email = ?", new String[]{email}, new UserRowMapper());
            return (u != null) ? getUserByRole(u.getRole(), new Long[]{u.getId()}, u) : null;
        } catch (EmptyResultDataAccessException e){
            return null;
        } catch (IncorrectResultSizeDataAccessException e){
            return null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public User getUser(String email, String hashedPassword) {
        try {
            User u = jdbcTemplate.queryForObject("select * from \"user\" where email = ? and password = ?", new String[]{email, hashedPassword}, new UserRowMapper());
            return (u != null) ? getUserByRole(u.getRole(), new Long[]{u.getId()}, u) : null;
        } catch (EmptyResultDataAccessException e) {
            return null;
        } catch (IncorrectResultSizeDataAccessException e){
            return null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean storeUser(Provider p, String hashedPassword) {
        try {
            // insert into user and keep id
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement("INSERT INTO \"user\"(email, password, role) VALUES (?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, p.getEmail());
                ps.setString(2, hashedPassword);
                ps.setString(3, "provider");
                return ps;
            }, keyHolder);
            long id = (long) keyHolder.getKeys().get("id");
            // use the same id to insert to provider
            jdbcTemplate.update("INSERT INTO provider (id, providername) VALUES (?, ?)", id, p.getProvidername());
            p.setId(id);
        }catch (Exception e) {
            System.err.println("Failed to store provider user");
            e.printStackTrace();
            System.out.println(e.getCause().getMessage());
            return false;
        }
        return true;
    }

    public boolean storeUser(Visitor v, String hashedPassword) {
        try {
            // insert into user and keep id
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement("INSERT INTO \"user\"(email, password, role) VALUES (?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, v.getEmail());
                ps.setString(2, hashedPassword);
                ps.setString(3, "visitor");
                return ps;
            }, keyHolder);
            long id = (long) keyHolder.getKeys().get("id");
            System.out.println(id);
            // use the same id to insert to visitor
            jdbcTemplate.update("INSERT INTO visitor (id, \"name\", surname) VALUES (?, ?, ?)", id, v.getName(), v.getSurname());
            v.setId(id);
        }catch (Exception e) {
            System.err.println("Failed to store visitor user");
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /* Another stupider but working way:
    public boolean storeUser(Provider p, String hashedPassword) {
        try {
            // insert to user
            jdbcTemplate.update("INSERT INTO \"user\"(email, password, role) VALUES (?, ?, ?)",
                                p.getEmail(), hashedPassword, "provider");
            // get the generated id from user (TODO: can this be done without a query?) NEEDs: email unique
            Long idwrapper = jdbcTemplate.queryForObject("SELECT id FROM \"user\" WHERE email = ?;", new String[]{p.getEmail()}, Long.class);
            if (idwrapper == null) throw new Exception();
            long id = idwrapper;
            // use the same id to insert to provider
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
            // insert to user
            jdbcTemplate.update("INSERT INTO \"user\"(email, password, role) VALUES (?, ?, ?)",
                    v.getEmail(), hashedPassword, "provider");
            // get the generated id from user (TODO: can this be done without a query?) NEEDs: email unique
            Long idwrapper = jdbcTemplate.queryForObject("SELECT id FROM \"user\" WHERE email = ?;", new String[]{v.getEmail()}, Long.class);
            if (idwrapper == null) throw new Exception();
            long id = idwrapper;
            // use the same id to insert to visitor
            jdbcTemplate.update("INSERT INTO visitor (id, \"name\", surname) VALUES (?, ?, ?)", id, v.getName(), v.getSurname());
            v.setId(id);
        } catch (Exception e) {
            System.err.println("Failed to store visitor user");
            e.printStackTrace();
            return false;
        }
        return true;
    }
    */

}