package back.data.jdbc;

import back.Exceptions.JTHDataBaseException;
import back.data.BookingDAO;
import back.model.Room;
import back.model.User;

import java.util.List;

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
    public boolean addRoomToFavourites(long visitorId, int roomId) throws JTHDataBaseException{
        return dataAccess.addRoomToFavourites(visitorId, roomId);
    }

    @Override
    public boolean removeRoomFromFavourites(long visitorId, int roomId) throws JTHDataBaseException {
        return dataAccess.removeRoomFromFavourites(visitorId, roomId);
    }

    @Override
    public List<Room> getFavouriteRoomIdsForVisitor(long visitorId) throws JTHDataBaseException {
        return dataAccess.getFavouriteRoomIdsForVisitor(visitorId);
    }

}
