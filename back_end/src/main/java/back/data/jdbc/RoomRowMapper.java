package back.data.jdbc;

import back.model.Location;
import back.model.Room;

import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RoomRowMapper implements RowMapper<Room> {

    @Override
    public Room mapRow(ResultSet rs, int rowNum) throws SQLException{
        int id = rs.getInt("id");
        long provider_id = rs.getLong("provider_id");
        double price = rs.getDouble("price");
        int capacity = rs.getInt("capacity");
        boolean wifi = rs.getBoolean("wifi");
        boolean pool = rs.getBoolean("pool");
        boolean shauna = rs.getBoolean("shauna");
        String roomName = rs.getString("room_name");
        //TODO: also add description to db + fix location
        Location location = null;
//        String geom = rs.getString("geom");
//        if (geom != null && !geom.equals("") && rs.getString("name") != null){
//            location = new Location(rs.getString("name"), -1, -1);
//            location.setCordX(geom);
//            location.setCordY(geom);
//        }
        String description = rs.getString("description");
        return new Room(id, roomName, provider_id, price, capacity, wifi, pool, shauna, location, description);
    }
}
