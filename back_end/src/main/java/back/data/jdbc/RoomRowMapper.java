package back.data.jdbc;

import back.Exceptions.JTHException;
import back.model.Location;
import back.model.Room;

import org.springframework.jdbc.core.RowMapper;
import javax.swing.tree.TreePath;
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
        //TODO: also add description to db + fix location
//        String geom = rs.getString("geom");
//        if (geom == null) System.err.println("NU:::LL");
//        Location location = new Location(rs.getString("name"), -1, -1);
//        location.setCordX(geom);
//        location.setCordY(geom);
        return new Room(id, provider_id, price, capacity, wifi, pool, shauna, null);
    }
}
