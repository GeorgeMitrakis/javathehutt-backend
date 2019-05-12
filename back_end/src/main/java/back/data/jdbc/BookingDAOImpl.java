package back.data.jdbc;

import back.data.BookingDAO;
import back.model.Room;
import back.model.User;

public class BookingDAOImpl implements BookingDAO {

    private final DataAccess dataAccess;

    public BookingDAOImpl(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    @Override
    public boolean bookRoomForVisitor(User user, Room room, String sqlStartDate, String sqlEndDate) {
        return dataAccess.insertTransaction(user, room, sqlStartDate, sqlEndDate);
    }

}