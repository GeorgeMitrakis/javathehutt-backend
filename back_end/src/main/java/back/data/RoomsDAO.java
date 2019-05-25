package back.data;

import back.Exceptions.JTHDataBaseException;
import back.model.Room;
import back.model.SearchConstraints;

import java.util.List;


public interface RoomsDAO {

    // Interface for accessing Room's part of the database

    Room getRoomById(int roomId) throws JTHDataBaseException;

    List<Room> searchRooms(SearchConstraints constraints) throws JTHDataBaseException;

}
