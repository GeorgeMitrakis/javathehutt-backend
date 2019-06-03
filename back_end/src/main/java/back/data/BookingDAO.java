package back.data;

import back.Exceptions.JTHDataBaseException;
import back.model.Room;
import back.model.User;
import back.model.Visitor;


public interface BookingDAO {

    // Interface for accessing the Book-keeping part of the database

    boolean bookRoomForVisitor(User user, Room room, String sqlStartDate, String sqlEndDate) throws JTHDataBaseException;

    boolean addRoomToFavourites(Visitor visitor, Room room) throws JTHDataBaseException;

    boolean removeRoomFromFavourites(Visitor visitor, int roomId) throws JTHDataBaseException;

}
