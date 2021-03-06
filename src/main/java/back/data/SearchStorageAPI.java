package back.data;

import back.exceptions.JTHDataBaseException;
import back.model.Room;
import back.model.SearchConstraints;
import back.model.Transaction;

import java.util.List;


public interface SearchStorageAPI {

    void pushRoom(Room room, List<Transaction> transactions) throws JTHDataBaseException;

    void pushTransaction(Room room, Transaction transaction) throws JTHDataBaseException;

    void deleteRoom(int roomId) throws JTHDataBaseException;

    List<Room> searchRooms(SearchConstraints constraints, int limit, int offset);

}
