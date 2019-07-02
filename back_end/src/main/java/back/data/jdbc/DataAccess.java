package back.data.jdbc;

import back.exceptions.JTHDataBaseException;
import back.model.*;
import back.util.DateHandler;
import org.apache.commons.dbcp2.BasicDataSource;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
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
        try {
            return jdbcTemplate.query("(SELECT \"user\".*, \"provider\".providername, '-' AS \"name\", '-' AS \"surname\" FROM \"user\", provider WHERE \"user\".id = provider.id)" +
                    " UNION ALL " +
                    "(SELECT \"user\".*, '-' AS providerName, visitor.name, visitor.surname FROM \"user\", visitor WHERE \"user\".id = visitor.id) LIMIT ? OFFSET ?", new Long[]{count, start}, new UltimateUserRowMapper());
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
                return jdbcTemplate.queryForObject("SELECT * FROM \"provider\" WHERE id = ?", par, new ProviderRowMapper(u));
            case "admin":
                return jdbcTemplate.queryForObject("SELECT * FROM administrator WHERE id = ?", par, new AdminRowMapper(u));
            default:
                return null;
        }
    }

    public boolean checkAuthentication(Long userId, String password) throws JTHDataBaseException {
        try {
            jdbcTemplate.queryForObject("SELECT 1 FROM \"user\" WHERE id = ? and password = ?", new Object[]{userId, password}, Boolean.class);
            return true;
        } catch (EmptyResultDataAccessException | InvalidDataAccessApiUsageException e){
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            throw new JTHDataBaseException();
        }
    }

    public void updateBasicUserInfo(long userId, String newemail, String newpassword) throws JTHDataBaseException {
        try {
            if (newemail != null && newpassword != null) jdbcTemplate.update("UPDATE \"user\" SET email = ?, \"password\" = ? WHERE id = ?", newemail, newpassword, userId);
            else if (newemail != null) jdbcTemplate.update("UPDATE \"user\" SET email = ? WHERE id = ?", newemail, userId);
            else if (newpassword != null) jdbcTemplate.update("UPDATE \"user\" SET \"password\" = ? WHERE id = ?", newpassword, userId);
        } catch (Exception e) {
            e.printStackTrace();
            throw new JTHDataBaseException();
        }
    }

    public void updateVisitor(long userId, String newname, String newsurname) throws JTHDataBaseException {
        try {
            if (newname != null && newsurname != null) jdbcTemplate.update("UPDATE visitor SET name = ?, surname = ? WHERE id = ?", newname, newsurname, userId);
            else if (newname != null)  jdbcTemplate.update("UPDATE visitor SET name = ? WHERE id = ?", newname, userId);
            else if (newsurname != null)  jdbcTemplate.update("UPDATE visitor SET surname = ? WHERE id = ?", newsurname, userId);
        } catch (Exception e) {
            e.printStackTrace();
            throw new JTHDataBaseException();
        }
    }

    public void updateProvider(long userId, String newProviderName) throws JTHDataBaseException {
        try {
            if (newProviderName != null) jdbcTemplate.update("UPDATE \"provider\" SET providerName = ? WHERE id = ?", newProviderName, userId);
        } catch (Exception e) {
            e.printStackTrace();
            throw new JTHDataBaseException();
        }
    }

    public User getUser(Long id) throws JTHDataBaseException {
        try {
            Long[] par = new Long[]{id};
            User u = jdbcTemplate.queryForObject("select * from \"user\" where id = ?", par, new UserRowMapper());
            return (u != null) ? getUserByRole(u.getRole(), par, u) : null;
        } catch (EmptyResultDataAccessException e) {
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
        } catch (Exception e) {
            e.printStackTrace();
            throw new JTHDataBaseException();
        }
    }

    public List<User> getUsersByEmailPrefix(String emailPrefix) throws JTHDataBaseException {
        try {
            return jdbcTemplate.query("(SELECT \"user\".*, \"provider\".providername, '-' AS \"name\", '-' AS \"surname\" FROM \"user\", \"provider\" WHERE \"user\".id = \"provider\".id AND email LIKE ?)" +
                    " UNION ALL " +
                    "(SELECT \"user\".*, '-' AS providerName, visitor.name, visitor.surname FROM \"user\", visitor WHERE \"user\".id = visitor.id AND email LIKE ?)", new Object[]{emailPrefix + "%", emailPrefix + "%"}, new UltimateUserRowMapper());
        } catch (Exception e) {
            e.printStackTrace();
            throw new JTHDataBaseException();
        }
    }

    public User getUser(String email, String hashedPassword) throws JTHDataBaseException {
        try {
            User u = jdbcTemplate.queryForObject("select * from \"user\" where email = ? and \"password\" = ?", new String[]{email, hashedPassword}, new UserRowMapper());
            return (u != null) ? getUserByRole(u.getRole(), new Long[]{u.getId()}, u) : null;
        } catch (EmptyResultDataAccessException e) {
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
                    jdbcTemplate.update("DELETE FROM \"provider\" WHERE id = ?", user.getId());
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
            // Note: make all transitive deletes in case there is no CASCADE in the database
            switch (user.getRole()) {
                case "provider":
                    jdbcTemplate.update("DELETE FROM \"provider\" WHERE id = ?", user.getId());
                    List<Room> hisRooms = getRoomsForProvider(user.getId());
                    for (Room room: hisRooms) {
                        removeRoom(room.getId());
                    }
                    break;
                case "visitor":
                    jdbcTemplate.update("DELETE FROM visitor WHERE id = ?", user.getId());
                    jdbcTemplate.update("DELETE FROM favorites WHERE visitor_id = ?", user.getId());
                    jdbcTemplate.update("DELETE FROM transactions WHERE visitor_id = ?", user.getId());
                    jdbcTemplate.update("DELETE FROM rating WHERE visitor_id = ?", user.getId());
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
            jdbcTemplate.update("INSERT INTO \"provider\" (id, providername) VALUES (?, ?)", id, p.getProvidername());
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

    public Room getRoom(int id) throws JTHDataBaseException {
        try {
            Object[] par = new Object[]{id};
            return jdbcTemplate.queryForObject("select room.*, \"location\".*, city.name from room, location, city where room.id = ? and room.location_id = location.id and location.city_id = city.id", par, new RoomRowMapper());
        } catch (EmptyResultDataAccessException e) {
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            throw new JTHDataBaseException();
        }
    }

    public List<Room> getRoomsForProvider(long providerId) throws JTHDataBaseException {
        try {
            return jdbcTemplate.query("SELECT room.*, \"location\".*, city.name FROM room, location, city WHERE provider_id = ? and room.location_id = location.id and location.city_id = city.id", new Object[]{providerId}, new RoomRowMapper());
        } catch (Exception e){
            e.printStackTrace();
            throw new JTHDataBaseException();
        }
    }

    public long insertTransaction(User user, Room room, String sqlStartDate, String sqlEndDate, int occupants) throws JTHDataBaseException {
        try {
            Boolean res = transactionTemplate.execute(status -> {
                if (occupants > room.getMaxOccupants()){
                    System.err.println("Failed to make transaction, too many occupants");
                    return false;
                }
                try {
                    if (room.getCapacity() <= countTransactions(room, sqlStartDate, sqlEndDate)) {
                        System.err.println("Failed to make transaction, no available rooms");
                        return false;
                    }
                } catch (JTHDataBaseException | IncorrectResultSizeDataAccessException e) {
                    return false;
                }
                jdbcTemplate.update("INSERT INTO transactions (id, visitor_id, room_id, cost, start_date, end_date, closure_date, occupants) VALUES (default , ?, ?, ?, ?::date, ?::date, ?::date, ?)", user.getId(), room.getId(), room.calcCostBasedOnOccupants(occupants), sqlStartDate, sqlEndDate, DateHandler.getSQLDateTimeNow(), occupants);
                return true;
            });
            if (res != null && !res) {
                return -1;  // not enough rooms
            }
        } catch (DuplicateKeyException e) {  // should not happen for new db with seperate id PK
            System.err.println("Tried to make a duplicate booking");
            return -1;
        } catch (Exception e) {
            e.printStackTrace();
            throw new JTHDataBaseException();
        }
        return jdbcTemplate.queryForObject("select id from transactions where room_id = ? and start_date = ? ::date and end_date = ? ::date", new Object[]{room.getId(), sqlStartDate, sqlEndDate}, Long.class);
    }

    public int countTransactions(Room room, String sqlStartDate, String sqlEndDate) throws JTHDataBaseException {
        try {
            Integer count = jdbcTemplate.queryForObject("select count(*) from \"transactions\" where room_id = ? and start_date <= ? ::date and end_date >= ? ::date", new Object[]{room.getId(), sqlStartDate, sqlEndDate}, Integer.class);
            return (count != null) ? count : -1;
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
            String query = "select room.*, \"location\".*, city.name from room, \"location\", city where \"location\".city_id = city.id and \"location\".id = room.location_id";

            // check for range
            if (constraints.getRange() != -1) query += " and ST_DWithin(location.geom, ST_GeomFromText('" + constraints.getLocation().getCoords() + "'), " + constraints.getRange() + ")";

            // check of occupants
            query += " and " + constraints.getOccupants() + " <= max_occupants";

            // check for price range
            if(constraints.getMaxCost() != -1) query += " and price <= "+ constraints.getMaxCost();
            if(constraints.getMinCost() != -1) query += " and price >= "+ constraints.getMinCost();

            // check for wifi
            if (constraints.getWifi()) query += " and wifi = true";

            // check for pool
            if (constraints.getPool()) query += " and pool = true";

            // check for shauna
            if (constraints.getShauna()) query += " and shauna = true";

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
        } catch (Exception e) {
            e.printStackTrace();
            throw new JTHDataBaseException();
        }
    }

    public Rating getRatingById(int ratingId) throws JTHDataBaseException {
        try {
            return jdbcTemplate.queryForObject("select * from rating where id = ?", new Object[]{ratingId}, new RatingRowMapper());
        } catch (IncorrectResultSizeDataAccessException e) {
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            throw new JTHDataBaseException();
        }
    }

    public int submitNewRoom(Room room) throws JTHDataBaseException {
        Location location = room.getLocation();
        Integer room_id = -1;
        try {
            room_id = transactionTemplate.execute(status -> {
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
                    PreparedStatement ps = connection.prepareStatement("INSERT INTO location (id, geom, city_id, cordX, cordY) VALUES (default, ST_GeomFromText(?), ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
                    ps.setString(1, location.getCoords());
                    ps.setInt(2, finalCityid);
                    ps.setDouble(3, location.getCordX());
                    ps.setDouble(4, location.getCordY());
                    return ps;
                }, keyHolder);
                location_id = (int) keyHolder.getKeys().get("id");

                // finally insert the room
                // Old way:
                //jdbcTemplate.update("INSERT INTO room (id, provider_id, location_id, capacity, price, wifi, pool, shauna, room_name, description, max_occupants) " +
                //                     "VALUES (default, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                //                     room.getProviderId(), location_id, room.getCapacity(), room.getPrice(), room.getWifi(), room.getPool(), room.getShauna(), room.getRoomName(), room.getDescription(), room.getMaxOccupants());
                keyHolder = new GeneratedKeyHolder();
                jdbcTemplate.update(connection -> {
                    PreparedStatement ps = connection.prepareStatement("INSERT INTO room (id, provider_id, location_id, capacity, price, wifi, pool, shauna, room_name, description, max_occupants) " +
                            "VALUES (default, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
                    ps.setLong(1, room.getProviderId());
                    ps.setInt(2, location_id);
                    ps.setDouble(3, room.getCapacity());
                    ps.setDouble(4, room.getPrice());
                    ps.setBoolean(5, room.getWifi());
                    ps.setBoolean(6, room.getPool());
                    ps.setBoolean(7, room.getShauna());
                    ps.setString(8, room.getRoomName());
                    ps.setString(9, room.getDescription());
                    ps.setInt(10, room.getMaxOccupants());
                    return ps;
                }, keyHolder);
                return (int) keyHolder.getKeys().get("id");
            });
        } catch (Exception e){
            e.printStackTrace();
            throw new JTHDataBaseException();
        }
        if (room_id != null && room_id != -1){
            room.setId(room_id);
            return room_id;
        } else return -1;
    }

    public boolean updateRoom(Room room) throws JTHDataBaseException {
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

                // then update location
                jdbcTemplate.update("UPDATE location SET city_id = ?, cordx = ?, cordY = ?, geom = ST_GeomFromText(?) WHERE id = ?", cityid, location.getCordX(), location.getCordY(), location.getCoords(), room.getLocationId());

                // finally update the room
                jdbcTemplate.update("UPDATE room SET location_id = ?, capacity = ?, price = ?, wifi = ?, pool = ?, shauna = ?, room_name = ?, description = ?, max_occupants = ? WHERE id = ?",
                        room.getLocationId(), room.getCapacity(), room.getPrice(), room.getWifi(), room.getPool(), room.getShauna(), room.getRoomName(), room.getDescription(), room.getMaxOccupants(), room.getId());
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
            // added all deletes in case there is no CASCADE set
            jdbcTemplate.update("DELETE FROM room WHERE id = ?", roomId);
            jdbcTemplate.update("DELETE FROM transactions WHERE room_id = ?", roomId);
            jdbcTemplate.update("DELETE FROM ratings WHERE room_id = ?", roomId);
            jdbcTemplate.update("DELETE FROM favorites WHERE room_id = ?", roomId);
            jdbcTemplate.update("DELETE FROM img WHERE room_id = ?", roomId);
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

    public List<Room> getFavouriteRoomIdsForVisitor(long visitorId) throws JTHDataBaseException {
        try {
            return jdbcTemplate.query("select room.* from room, favorites, visitor " +
                                          "where room.id = favorites.room_id and favorites.visitor_id = visitor.id and visitor.id = ?", new Object[]{visitorId}, new RoomRowMapper());
        } catch (Exception e) {
            e.printStackTrace();
            throw new JTHDataBaseException();
        }
    }

    public Provider getProviderForRoom(int roomId) throws JTHDataBaseException {
        try {
            User user = jdbcTemplate.queryForObject("select \"user\".* from \"user\", provider, room where \"user\".id = provider.id and room.provider_id = provider.id and room.id = ?", new Object[]{roomId}, new UserRowMapper());
            if (user == null) return null;
            return jdbcTemplate.queryForObject("select * from provider where id = ?", new Object[]{user.getId()}, new ProviderRowMapper(user));
        } catch (IncorrectResultSizeDataAccessException e) {
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            throw new JTHDataBaseException();
        }
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

    public Image getImageById(long imgId) throws JTHDataBaseException {
        try{
            Image img = jdbcTemplate.queryForObject("SELECT * FROM img where id = ?", new Long[]{imgId}, new ImageRowMapper());
            return img;
        }catch (Exception e){
            e.printStackTrace();
            throw new JTHDataBaseException();
        }
    }

    public List<Long> getRoomImageIds(long roomId) throws JTHDataBaseException {
        List<Long> results;
        try {
            results = jdbcTemplate.queryForList("SELECT id FROM img WHERE room_id = ?", new Long[]{roomId}, Long.class);
        } catch (Exception e){
            e.printStackTrace();
            throw new JTHDataBaseException();
        }
        return results;
    }

    public List<Transaction> getTransactions() throws JTHDataBaseException {
        List<Transaction> results;
        try {
            results = jdbcTemplate.query("SELECT * FROM transactions ", new TransactionRowMapper());
        } catch (Exception e){
            e.printStackTrace();
            throw new JTHDataBaseException();
        }
        return results;
    }

    public List<Transaction> getTransactionsForRoom(int roomId) throws JTHDataBaseException {
        List<Transaction> results;
        try {
            results = jdbcTemplate.query("SELECT * FROM transactions WHERE room_id = ?", new Object[]{roomId}, new TransactionRowMapper());
        } catch (Exception e){
            e.printStackTrace();
            throw new JTHDataBaseException();
        }
        return results;
    }

    public List<Transaction> getTransactionsForProvider(long providerId) throws JTHDataBaseException {
        List<Transaction> results;
        try {
            results = jdbcTemplate.query("SELECT * FROM transactions, room WHERE transactions.room_id = room.id AND room.provider_id = ?", new Object[]{providerId}, new TransactionRowMapper());
        } catch (Exception e){
            e.printStackTrace();
            throw new JTHDataBaseException();
        }
        return results;
    }

    public List<Transaction> getTransactionsForVisitor(long visitorId) throws JTHDataBaseException {
        List<Transaction> results;
        try {
            results = jdbcTemplate.query("SELECT * FROM transactions WHERE visitor_id = ?", new Object[]{visitorId}, new TransactionRowMapper());
        } catch (Exception e){
            e.printStackTrace();
            throw new JTHDataBaseException();
        }
        return results;
    }

    public double sumTransactionCosts() throws JTHDataBaseException {
        try{
            Double sum = jdbcTemplate.queryForObject("SELECT SUM(cost) FROM transactions", Double.class);
            return (sum != null) ? sum : 0.0;
        } catch (IncorrectResultSizeDataAccessException e) {
            return 0.0;
        } catch (Exception e){
            e.printStackTrace();
            throw new JTHDataBaseException();
        }
    }

    public double sumTransactionCosts(long providerId) throws JTHDataBaseException {
        try{
            Double sum = jdbcTemplate.queryForObject("SELECT SUM(transactions.cost) FROM transactions, room WHERE transactions.room_id = room.id and room.provider_id = ?", new Long[]{providerId}, Double.class);
            return (sum != null) ? sum : 0.0;
        } catch (IncorrectResultSizeDataAccessException e) {
            return 0.0;
        } catch (Exception e){
            e.printStackTrace();
            throw new JTHDataBaseException();
        }
    }
}