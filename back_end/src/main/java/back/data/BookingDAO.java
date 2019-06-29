package back.data;

import back.exceptions.JTHDataBaseException;
import back.model.Room;
import back.model.Transaction;
import back.model.User;

import java.util.List;


public interface BookingDAO {

    // Interface for accessing the Book-keeping part of the database

    boolean bookRoomForVisitor(User user, Room room, String sqlStartDate, String sqlEndDate) throws JTHDataBaseException;

    boolean addRoomToFavourites(long visitorId, int roomId) throws JTHDataBaseException;

    boolean removeRoomFromFavourites(long visitorId, int roomId) throws JTHDataBaseException;

    List<Room> getFavouriteRoomIdsForVisitor(long visitorId) throws JTHDataBaseException;

    List<Transaction> getTransactions() throws JTHDataBaseException;

    double lefta() throws JTHDataBaseException;

}
