package back.data.jdbc;

import back.model.Rating;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class RatingRowMapper implements RowMapper<Rating> {

    @Override
    public Rating mapRow(ResultSet rs, int rowNum) throws SQLException {
        int id = rs.getInt("id");
        int room_id = rs.getInt("room_id");
        long visitor_id = rs.getLong("visitor_id");
        int stars = rs.getInt("stars");
        String comment = rs.getString("comment");
        return new Rating(id, room_id, visitor_id, stars, comment);
    }

}
