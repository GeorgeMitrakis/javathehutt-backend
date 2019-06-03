package back.data;

import back.Exceptions.JTHDataBaseException;
import back.model.Provider;
import back.model.Room;
import back.model.SearchConstraints;
import back.model.Visitor;

import java.util.List;


public interface RoomsDAO {

    // Interface for accessing Room's part of the database

    Room getRoomById(int roomId) throws JTHDataBaseException;

    List<Room> searchRooms(SearchConstraints constraints) throws JTHDataBaseException;

    boolean addRatingToRoom(Visitor visitor, Room room, int stars, String comment) throws JTHDataBaseException;

    void removeRatingFromRoom(int ratingId) throws JTHDataBaseException;

    boolean submitNewRoom(Provider provider, Room room) throws JTHDataBaseException;

    void removeRoom(Room room) throws JTHDataBaseException;

}
