package back.data;

import back.data.jdbc.DataAccess;
import back.exceptions.JTHDataBaseException;
import back.model.Provider;
import back.model.Rating;
import back.model.Room;
import back.model.SearchConstraints;

import java.util.List;

public class RoomsDAOImpl implements RoomsDAO {

    private final DataAccess dataAccess;
    private final SearchStorageImplementation search;

    public RoomsDAOImpl(DataAccess dataAccess, SearchStorageImplementation search) {
        this.dataAccess = dataAccess;
        this.search = search;
    }

    @Override
    public Room getRoomById(int roomId) throws JTHDataBaseException {
        return dataAccess.getRoom(roomId);
    }

    @Override
    public List<Room> getRoomsForProvider(long providerId) throws JTHDataBaseException {
        return dataAccess.getRoomsForProvider(providerId);
    }

    @Override
    public List<Room> searchRooms(SearchConstraints constraints) throws JTHDataBaseException {
        List<Room> results = search.searchRooms(constraints, -1, -1);
        if (results == null) results = dataAccess.searchRooms(constraints);   // if not implemented in search
        return results;
    }

    @Override
    public boolean addRatingToRoom(long visitorId, int roomId, int stars, String comment) throws JTHDataBaseException {
        if (stars < 0 || stars > 5) return false;
        dataAccess.addRatingToRoom(visitorId, roomId, stars, comment);
        return true;
    }

    @Override
    public void removeRatingFromRoom(int ratingId) throws JTHDataBaseException {
        dataAccess.removeRatingFromRoom(ratingId);
    }

    @Override
    public List<Rating> getRatingsForRoom(int roomId) throws JTHDataBaseException {
        return dataAccess.getRatingsForRoom(roomId);
    }

    @Override
    public Rating getRatingById(int ratingId) throws JTHDataBaseException {
        return dataAccess.getRatingById(ratingId);
    }

    @Override
    public int submitNewRoom(Room room) throws JTHDataBaseException {
        int roomId = dataAccess.submitNewRoom(room);
        if (roomId != -1) search.pushRoom(room, null);
        return roomId;
    }

    @Override
    public boolean updateRoom(Room room) throws JTHDataBaseException {
        boolean succ = dataAccess.updateRoom(room);
        if (succ) search.pushRoom(room, dataAccess.getTransactionsForRoom(room.getId()));
        return succ;
    }

    @Override
    public void removeRoom(int roomId) throws JTHDataBaseException {
        dataAccess.removeRoom(roomId);
        search.deleteRoom(roomId);
    }

    @Override
    public List<String> autocompletePrefix(String prefix) throws JTHDataBaseException {
        return dataAccess.autocompletePrefix(prefix);
    }

    @Override
    public Provider getProviderForRoom(int roomId) throws JTHDataBaseException {
        return dataAccess.getProviderForRoom(roomId);
    }

}
