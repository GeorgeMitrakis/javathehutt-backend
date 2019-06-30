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
        int location_id = rs.getInt("location_id");
        double price = rs.getDouble("price");
        int capacity = rs.getInt("capacity");
        boolean wifi = rs.getBoolean("wifi");
        boolean pool = rs.getBoolean("pool");
        boolean shauna = rs.getBoolean("shauna");
        String roomName = rs.getString("room_name");
        Location location = new Location(rs.getString("name"), rs.getDouble("cordX"), rs.getDouble("cordY"));
        String description = rs.getString("description");
        int maxOccupants = rs.getInt("max_occupants");
        return new Room(id, roomName, provider_id, location_id, price, capacity, wifi, pool, shauna, location, description, maxOccupants, true);
    }
}
