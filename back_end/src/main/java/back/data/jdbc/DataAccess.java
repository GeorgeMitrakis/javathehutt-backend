package back.data.jdbc;

import back.Exceptions.JTHDataBaseException;
import back.model.*;
import back.util.DateHandler;
import org.apache.commons.dbcp2.BasicDataSource;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;
import java.sql.*;
import java.util.List;


public class DataAccess {

    private static final int MAX_TOTAL_CONNECTIONS = 16;
    private static final int MAX_IDLE_CONNECTIONS = 8;

    private DataSource dataSource;
    private JdbcTemplate jdbcTemplate;
    private TransactionTemplate transactionTemplate;

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

        //Transaction manager
        DataSourceTransactionManager transactionManager = new DataSourceTransactionManager(bds);

        //initialize the jdbc template utility
        jdbcTemplate = new JdbcTemplate(bds);

        //initialize the transaction template utility
        transactionTemplate = new TransactionTemplate(transactionManager);

        //keep the dataSource for the low-level manual example to function (not actually required)
        dataSource = bds;
    }

    public long countUsers() throws JTHDataBaseException {
        try {
            return jdbcTemplate.queryForObject("select count(*) from \"user\"", Long.class);
        } catch (Exception e) {
            e.printStackTrace();
            throw new JTHDataBaseException();
        }
    }

    public List<User> getUsers(long start, long count) throws JTHDataBaseException {
        // TODO return extended objects: ex Visitor?
        try {
            Long[] params = new Long[]{count, start};
            return jdbcTemplate.query("select * from \"user\" limit ? offset ?", params, new UserRowMapper());
        } catch (Exception e) {
            e.printStackTrace();
            throw new JTHDataBaseException();
        }
    }

    private User getUserByRole(String role, Object[] par, User u) throws EmptyResultDataAccessException, IncorrectResultSizeDataAccessException {
        switch (role) {
            case "visitor":
                return jdbcTemplate.queryForObject("SELECT * FROM visitor WHERE id = ?", par, new VisitorRowMapper(u));
            case "provider":
                return jdbcTemplate.queryForObject("SELECT * FROM provider WHERE id = ?", par, new ProviderRowMapper(u));
            case "admin":
                return jdbcTemplate.queryForObject("SELECT * FROM administrator WHERE id = ?", par, new AdminRowMapper(u));
            default:
                return null;
        }
    }

    public User getUser(Long id) throws JTHDataBaseException {
        try {
            Long[] par = new Long[]{id};
            User u = jdbcTemplate.queryForObject("select * from \"user\" where id = ?", par, new UserRowMapper());
            return (u != null) ? getUserByRole(u.getRole(), par, u) : null;
        } catch (EmptyResultDataAccessException e) {
            return null;
        } catch (IncorrectResultSizeDataAccessException e) {
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            throw new JTHDataBaseException();
        }
    }

    public User getUser(String email) throws JTHDataBaseException {
        try {
            User u = jdbcTemplate.queryForObject("select * from \"user\" where email = ?", new String[]{email}, new UserRowMapper());
            return (u != null) ? getUserByRole(u.getRole(), new Long[]{u.getId()}, u) : null;
        } catch (EmptyResultDataAccessException e) {
            return null;
        } catch (IncorrectResultSizeDataAccessException e) {
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            throw new JTHDataBaseException();
        }
    }

    public User getUser(String email, String hashedPassword) throws JTHDataBaseException {
        try {
            User u = jdbcTemplate.queryForObject("select * from \"user\" where email = ? and password = ?", new String[]{email, hashedPassword}, new UserRowMapper());
            return (u != null) ? getUserByRole(u.getRole(), new Long[]{u.getId()}, u) : null;
        } catch (EmptyResultDataAccessException e) {
            return null;
        } catch (IncorrectResultSizeDataAccessException e) {
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            throw new JTHDataBaseException();
        }
    }

    public boolean getUserBan(long id) throws JTHDataBaseException {
        try {
            Long[] par = new Long[]{id};
            Boolean result = jdbcTemplate.queryForObject("select isbanned from \"user\" where id = ?", par, Boolean.class);
            return (result != null) ? result.booleanValue() : true;
        } catch (IncorrectResultSizeDataAccessException ignored) {
        } catch (Exception e) {
            e.printStackTrace();
            throw new JTHDataBaseException();
        }
        return false;
    }

    public void setUserBan(User user, boolean ban) throws JTHDataBaseException {
        try {
            jdbcTemplate.update("UPDATE \"user\" SET isBanned = ? WHERE id = ?", ban, user.getId());
        } catch (Exception e) {
            e.printStackTrace();
            throw new JTHDataBaseException();
        }
    }

    public boolean promoteUser(User user) throws JTHDataBaseException {
        try {
            switch (user.getRole()) {
                case "provider":
                    jdbcTemplate.update("DELETE FROM provider WHERE id = ?", user.getId());
                    jdbcTemplate.update("UPDATE \"user\" SET role = 'admin' WHERE id = ?", user.getId());
                    jdbcTemplate.update("INSERT INTO administrator (id, name, surname) VALUES (?, ?, ?)",
                            user.getId(), "Guy from " + ((Provider) user).getProvidername(), "Guy from " + ((Provider) user).getProvidername());  // TODO what name to give?
                    break;
                case "visitor":
                    jdbcTemplate.update("DELETE FROM visitor WHERE id = ?", user.getId());
                    jdbcTemplate.update("UPDATE \"user\" SET role = 'admin' WHERE id = ?", user.getId());
                    jdbcTemplate.update("INSERT INTO administrator (id, name, surname) VALUES (?, ?, ?)",
                            user.getId(), ((Visitor) user).getName(), ((Visitor) user).getSurname());
                    break;
                case "admin":
                    System.out.println("Warning: Tried to promote user who is already an admin");
                    return true;
                default:
                    System.err.println("Warning: wrong role in User object or in promoteUser() code!");
                    return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new JTHDataBaseException();
        }
        return true;
    }

    public boolean deleteUser(User user) throws JTHDataBaseException {
        try {
            switch (user.getRole()) {
                case "provider":
                    jdbcTemplate.update("DELETE FROM provider WHERE id = ?", user.getId());
                    break;
                case "visitor":
                    jdbcTemplate.update("DELETE FROM visitor WHERE id = ?", user.getId());
                    break;
                case "admin":
                    System.out.println("Warning: Deleting an administrator!");
                    jdbcTemplate.update("DELETE FROM administrator WHERE id = ?", user.getId());
                    break;
                default:
                    System.err.println("Warning: wrong role in User object or in deleteUser() code!");
                    return false;
            }
            jdbcTemplate.update("DELETE FROM \"user\" WHERE id = ?", user.getId());
        } catch (Exception e) {
            e.printStackTrace();
            throw new JTHDataBaseException();
        }
        return true;
    }

    public void storeUser(Provider p, String hashedPassword) throws JTHDataBaseException {
        try {
            // insert into user and keep id
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement("INSERT INTO \"user\"(id, email, password, role) VALUES (default, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, p.getEmail());
                ps.setString(2, hashedPassword);
                ps.setString(3, "provider");
                return ps;
            }, keyHolder);
            long id = (long) keyHolder.getKeys().get("id");
            // use the same id to insert to provider
            jdbcTemplate.update("INSERT INTO provider (id, providername) VALUES (?, ?)", id, p.getProvidername());
            p.setId(id);
        } catch (Exception e) {
            System.err.println("Failed to store provider user");
            e.printStackTrace();
            throw new JTHDataBaseException();
        }
    }

    public void storeUser(Visitor v, String hashedPassword) throws JTHDataBaseException {
        try {
            // insert into user and keep id
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement("INSERT INTO \"user\"(id, email, password, role) VALUES (default, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, v.getEmail());
                ps.setString(2, hashedPassword);
                ps.setString(3, "visitor");
                return ps;
            }, keyHolder);
            long id = (long) keyHolder.getKeys().get("id");
            // use the same id to insert to visitor
            jdbcTemplate.update("INSERT INTO visitor (id, \"name\", surname) VALUES (?, ?, ?)", id, v.getName(), v.getSurname());
            v.setId(id);
        } catch (Exception e) {
            System.err.println("Failed to store visitor user");
            e.printStackTrace();
            throw new JTHDataBaseException();
        }
    }

    public Room getRoom(long id) throws JTHDataBaseException {
        try {
            Long[] par = new Long[]{id};
            return jdbcTemplate.queryForObject("select room.*, location.*, city.name from room, location, city where room.id = ? and room.location_id = location.id and location.city_id = city.id", par, new RoomRowMapper());
        } catch (EmptyResultDataAccessException e) {
            return null;
        } catch (IncorrectResultSizeDataAccessException e) {
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            throw new JTHDataBaseException();
        }
    }

    public boolean insertTransaction(User user, Room room, String sqlStartDate, String sqlEndDate) throws JTHDataBaseException {
        try {
            Boolean res = transactionTemplate.execute(status -> {
                try {
                    if (room.getCapacity() <= countTransactions(room, sqlStartDate, sqlEndDate)) {
                        System.err.println("Failed to make transaction, no available rooms");
                        return false;
                    }
                } catch (JTHDataBaseException e) {
                    return false;
                }
                jdbcTemplate.update("INSERT INTO transactions (id, visitor_id, room_id, cost, start_date, end_date, closure_date) VALUES (default , ?, ?, ?, ?::date, ?::date, ?::date)", user.getId(), room.getId(), room.getPrice(), sqlStartDate, sqlEndDate, DateHandler.getSQLDateTimeNow());
                return true;
            });
            if (res != null && !res) {
                return false;  // not enough rooms
            }
        } catch (DuplicateKeyException e) {  // should not happen for new db with seperate id PK
            System.err.println("Tried to make a duplicate booking");
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            throw new JTHDataBaseException();
        }
        return true;
    }

    public int countTransactions(Room room, String sqlStartDate, String sqlEndDate) throws JTHDataBaseException {
        try {
            Integer count = jdbcTemplate.queryForObject("select count(*) from \"transactions\" where room_id = ? and start_date <= ? ::date and end_date >= ? ::date", new Object[]{room.getId(), sqlStartDate, sqlEndDate}, Integer.class);
            return (count != null) ? count : -1;
        } catch (EmptyResultDataAccessException e) {
            return -1;
        } catch (IncorrectResultSizeDataAccessException e) {
            return -1;
        } catch (Exception e) {
            e.printStackTrace();
            throw new JTHDataBaseException();
        }
    }

    public List<Room> searchRooms(SearchConstraints constraints) throws JTHDataBaseException {
        List<Room> results;
        try {
            String query = "select room.*, location.*, city.name from room, location, city where location.city_id = city.id and ";

            // check for range
            if(constraints.getRange() != -1) query += "location.id = room.location_id and ST_DWithin(location.geom, ST_GeomFromText('" + constraints.getLocation().getCoords() + "'), " + constraints.getRange() + ") and ";

            // check for price range
            if(constraints.getMaxCost() != -1) query += "price <= "+ constraints.getMaxCost() +" and ";
            if(constraints.getMinCost() != -1) query += "price >= "+ constraints.getMinCost() +" and ";

            // check for wifi
            if(constraints.getWifi()) query += "wifi = true and ";

            // check for pool
            if(constraints.getPool()) query += "pool = true and ";

            // check for shauna
            if(constraints.getShauna()) query += "shauna = true and ";

            query += "1=1";

            results = jdbcTemplate.query(query, new RoomRowMapper());
        } catch (Exception e) {
            e.printStackTrace();
            throw new JTHDataBaseException();
        }
        return results;
    }

    public void addRatingToRoom(long visitorId, int roomId, int stars, String comment) throws JTHDataBaseException {
        try {
            jdbcTemplate.update("INSERT INTO rating (id, comment, stars, room_id, visitor_id) VALUES (default, ?, ?, ?, ?)",
                                     comment, stars, roomId, visitorId);
        } catch (Exception e){
            e.printStackTrace();
            throw new JTHDataBaseException();
        }
    }

    public void removeRatingFromRoom(int ratingId) throws JTHDataBaseException {
        try {
            jdbcTemplate.update("DELETE FROM rating WHERE id = ?", ratingId);
        } catch (Exception e){
            e.printStackTrace();
            throw new JTHDataBaseException();
        }
    }

    public List<Rating> getRatingsForRoom(int roomId) throws JTHDataBaseException {

        try {
            return jdbcTemplate.query("select * from rating where room_id = ?", new Object[]{roomId}, new RatingRowMapper());
        } catch (EmptyResultDataAccessException e) {
            return null;
        } catch (IncorrectResultSizeDataAccessException e) {
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            throw new JTHDataBaseException();
        }
    }

    public boolean submitNewRoom(Room room) throws JTHDataBaseException {
        Location location = room.getLocation();
        try {
            transactionTemplate.execute(status -> {
                // if the city name does not exists already then insert it and keep id
                int cityid;
                try {
                    // try querying for object to see if it exists
                    cityid = jdbcTemplate.queryForObject("SELECT id FROM city WHERE name = ?", new Object[]{location.getCityname()}, Integer.class);
                } catch (NullPointerException | IncorrectResultSizeDataAccessException e){
                    KeyHolder keyHolder = new GeneratedKeyHolder();
                    jdbcTemplate.update(connection -> {
                        PreparedStatement ps = connection.prepareStatement("INSERT INTO city (id, name) VALUES (default, ?)", Statement.RETURN_GENERATED_KEYS);
                        ps.setString(1, location.getCityname());
                        return ps;
                    }, keyHolder);
                    cityid = (int) keyHolder.getKeys().get("id");
                }

                // then insert location
                int location_id;
                KeyHolder keyHolder = new GeneratedKeyHolder();
                int finalCityid = cityid;   // dunno why this was needed
                jdbcTemplate.update(connection -> {
                    PreparedStatement ps = connection.prepareStatement("INSERT INTO location (id, geom, city_id) VALUES (default, ST_GeomFromText(?), ?)", Statement.RETURN_GENERATED_KEYS);
                    ps.setString(1, location.getCoords());
                    ps.setInt(2, finalCityid);
                    return ps;
                }, keyHolder);
                location_id = (int) keyHolder.getKeys().get("id");

                // finally the room
                jdbcTemplate.update("INSERT INTO room (id, provider_id, location_id, capacity, price, wifi, pool, shauna) " +
                                     "VALUES (default, ?, ?, ?, ?, ?, ?, ?)",
                                     room.getProviderId(), location_id, room.getCapacity(), room.getPrice(), room.getWifi(), room.getPool(), room.getShauna());
                return true;
            });
        } catch (Exception e){
            e.printStackTrace();
            throw new JTHDataBaseException();
        }
        return true;
    }

    public void removeRoom(int roomId) throws JTHDataBaseException {
        try {
            // CASCADE option should take care of all the necessary deletes on other tables like location and city (TODO: check)
            jdbcTemplate.update("DELETE FROM room WHERE id = ?", roomId);
        } catch (Exception e){
            e.printStackTrace();
            throw new JTHDataBaseException();
        }
    }

    public boolean addRoomToFavourites(long visitorId, int roomId) throws JTHDataBaseException{
        try {
            jdbcTemplate.update("INSERT INTO favorites (visitor_id, room_id) VALUES (?, ?)", visitorId, roomId);
        } catch (Exception e){
            e.printStackTrace();
            throw new JTHDataBaseException();
        }
        return true;
    }

    public boolean removeRoomFromFavourites(long visitorId, int roomId) throws JTHDataBaseException {
        try {
            jdbcTemplate.update("DELETE FROM favorites WHERE visitor_id = ? and room_id = ?", visitorId, roomId);
        } catch (Exception e){
            e.printStackTrace();
            throw new JTHDataBaseException();
        }
        return true;
    }

    public List<String> autocompletePrefix(String prefix) throws JTHDataBaseException {
        List<String> results;
        try {
            results = jdbcTemplate.queryForList("SELECT name FROM city WHERE name LIKE ?", new Object[]{prefix + "%"}, String.class);
        } catch (Exception e){
            e.printStackTrace();
            throw new JTHDataBaseException();
        }
        return results;
    }

}