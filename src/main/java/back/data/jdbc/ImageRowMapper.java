package back.data.jdbc;

import back.model.Image;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ImageRowMapper implements RowMapper<Image> {

    @Override
    public Image mapRow(ResultSet rs, int rowNum) throws SQLException {
        long id = rs.getLong("id");
        long roomId = rs.getLong("room_id");
        String url = rs.getString("url");
        return new Image(id, url, roomId);
    }
}
