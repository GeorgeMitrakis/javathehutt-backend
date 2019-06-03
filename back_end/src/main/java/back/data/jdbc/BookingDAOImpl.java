package back.data.jdbc;

import back.Exceptions.JTHDataBaseException;
import back.data.BookingDAO;
import back.model.Room;
import back.model.User;
import back.model.Visitor;

public class BookingDAOImpl implements BookingDAO {

    private final DataAccess dataAccess;

    public BookingDAOImpl(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    @Override
    public boolean bookRoomForVisitor(User user, Room room, String sqlStartDate, String sqlEndDate) throws JTHDataBaseException {
        return dataAccess.insertTransaction(user, room, sqlStartDate, sqlEndDate);
    }

    @Override
    public boolean addRoomToFavourites(Visitor visitor, Room room) throws JTHDataBaseException{
        return dataAccess.addRoomToFavourites(visitor, room);
    }

    @Override
    public boolean removeRoomFromFavourites(Visitor visitor, int roomId) throws JTHDataBaseException {
        return dataAccess.removeRoomFromFavourites(visitor, roomId);
    }

}
