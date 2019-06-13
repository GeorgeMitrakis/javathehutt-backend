package back.data.jdbc;

import back.model.Image;
import back.model.Transaction;
import back.util.DateHandler;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TransactionRowMapper implements RowMapper<Transaction> {

    @Override
    public Transaction mapRow(ResultSet rs, int rowNum) throws SQLException {
        int id = rs.getInt("id");
        int roomId = rs.getInt("room_id");
        int visitorId = rs.getInt("visitor_id");
        double cost = rs.getDouble("cost");
        String startDate = DateHandler.SQLDateToFrontDate(rs.getString("start_date"));
        String endDate = DateHandler.SQLDateToFrontDate(rs.getString("end_date"));
        return new Transaction(visitorId, roomId, id, startDate, endDate, cost);
    }
}