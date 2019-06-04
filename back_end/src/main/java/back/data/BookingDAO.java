package back.data;

import back.Exceptions.JTHDataBaseException;
import back.model.Room;
import back.model.User;


public interface BookingDAO {

    // Interface for accessing the Book-keeping part of the database

    boolean bookRoomForVisitor(User user, Room room, String sqlStartDate, String sqlEndDate) throws JTHDataBaseException;

    boolean addRoomToFavourites(long visitorId, int roomId) throws JTHDataBaseException;

    boolean removeRoomFromFavourites(long visitorId, int roomId) throws JTHDataBaseException;

}
