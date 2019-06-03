package back.data.jdbc;

import back.model.Location;
import back.model.Room;

import org.springframework.jdbc.core.RowMapper;
import javax.swing.tree.TreePath;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RoomRowMapper implements RowMapper<Room> {

    @Override
    public Room mapRow(ResultSet rs, int rowNum) throws SQLException{
        long id = rs.getLong("id");
        long provider_id = rs.getLong("provider_id");
        double price = rs.getDouble("price");
        int capacity = rs.getInt("capacity");
        boolean wifi = rs.getBoolean("wifi");
        boolean pool = rs.getBoolean("pool");
        boolean shauna = rs.getBoolean("shauna");
        //TODO: also add description to db
        Location location = new Location(rs.getString("city.name"), rs.getDouble("coordinate_X"), rs.getDouble("coordinate_Y"));
        return new Room(id, provider_id, price, capacity, wifi, pool, shauna, location);
    }
}
