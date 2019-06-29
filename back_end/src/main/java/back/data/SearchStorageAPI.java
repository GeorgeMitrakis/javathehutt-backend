package back.data;

import back.model.Room;
import back.model.SearchConstraints;
import back.model.Transaction;

import java.util.List;


public interface SearchStorageAPI {

    void pushRoom(Room room, List<Transaction> transactions);

    void pushTransaction(int roomId, Transaction transaction);

    void deleteRoom(int roomId);

    List<Room> searchRooms(SearchConstraints constraints, int limit, int offset);

    List<String> autocomplete(String prefix);  // Maybe (leave it for last)

    void pushSQLtoSearchStorage();             // pushes the whole SQL Data Base to search storage (leave it for last)

}
