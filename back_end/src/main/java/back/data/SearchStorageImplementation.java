package back.data;

import back.model.Room;
import back.model.SearchConstraints;
import back.model.Transaction;
import java.util.List;


public class SearchStorageImplementation implements SearchStorageAPI {

    @Override
    public void pushRoom(Room room, List<Transaction> transactions) {
        // TODO
    }

    @Override
    public void pushTransaction(int roomId, Transaction transaction) {
        // TODO
    }

    @Override
    public void deleteRoom(int roomId) {
        // TODO
    }

    @Override
    public List<Room> searchRooms(SearchConstraints constraints, int limit, int offset) {
        // TODO
        return null;
    }

    @Override
    public List<String> autocomplete(String prefix) {
        // TODO (last)
        return null;
    }

    @Override
    public void pushSQLtoSearchStorage() {
        // TODO (last)
    }

}
