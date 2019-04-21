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
    private static final int MAX_IDLE_CONNECTIONS   = 8;
    
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
        Long[] params = new Long[]{id};
        List<User> users = jdbcTemplate.query("select * from \"user\" where id = ?", params, new UserRowMapper());
        if (users.size() == 1)  {
            return Optional.of(users.get(0));
        }
        else {
            return Optional.empty();
        }
    }

    public Optional<User> getUser(String email, String hashedPassword){
        String[] params = {email, hashedPassword};
        User u = jdbcTemplate.queryForObject("select * from \"user\" where email = ? and hashedPassword = ?", params, new UserRowMapper());
        if(u == null) return Optional.empty();
        String role = u.getRole();
        String[] par = {Long.toString(u.getId())};
        switch(role){
            case "visitor":
                String sql = "select usr.id, usr.email, usr.name, usr.surname, usr.hashedpassword, usr.role from \"user\" as usr " +
                        "left join visitor p on usr.id = p.id " +
                        "where usr.id == ?";
                Visitor v = jdbcTemplate.queryForObject(sql, par, new VisitorRowMapper());
                return ( v == null) ? Optional.of(v) : Optional.empty();
            case "provider":
                String sql2 = "select usr.id, usr.email, usr.name, usr.surname, usr.hashedpassword, usr.role from \"user\" as usr " +
                        "left join provider p on usr.id = p.id " +
                        "where usr.id == ?";
                Provider p = jdbcTemplate.queryForObject(sql2, par , new ProviderRowMapper());
                return (p == null) ? Optional.of(p) : Optional.empty();
        }
        return Optional.empty();

    }

    public void storeUser(Provider p, String password) {

        try {
            jdbcTemplate.update("INSERT INTO \"user\"(email, name, surname, hashedpassword, role) VALUES (?,?,?,?,?)",
                    p.getEmail(),
                    p.getName(),
                    p.getSurname(),
                    JTHSecurity.makeSHA(password),
                    "provider"
            );
            jdbcTemplate.update("INSERT INTO \"provider\" (id) VALUES ( ? )",p.getId());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void storeUser(Visitor v, String password) {

        try {
            jdbcTemplate.update("INSERT INTO \"user\"(email, name, surname, hashedpassword, role) VALUES (?,?,?,?,?)",
                    v.getEmail(),
                    v.getName(),
                    v.getSurname(),
                    JTHSecurity.makeSHA(password),
                    "provider"
            );
            jdbcTemplate.update("INSERT INTO \"visitor\" (id) VALUES ( ? )",v.getId());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}