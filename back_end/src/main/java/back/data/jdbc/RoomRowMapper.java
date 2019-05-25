package back.data.jdbc;

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
        return new Room(id, provider_id, price, capacity);
    }
}
