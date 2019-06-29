package back.data;

import back.data.jdbc.DataAccess;
import back.exceptions.JTHDataBaseException;
import back.data.RoomsDAO;
import back.model.Provider;
import back.model.Rating;
import back.model.Room;
import back.model.SearchConstraints;

import java.util.List;

public class RoomsDAOImpl implements RoomsDAO {

    private final DataAccess dataAccess;

    public RoomsDAOImpl(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    @Override
    public Room getRoomById(int roomId) throws JTHDataBaseException {
        return dataAccess.getRoom(roomId);
    }

    @Override
    public List<Room> searchRooms(SearchConstraints constraints) throws JTHDataBaseException {
        return dataAccess.searchRooms(constraints);
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
    public boolean submitNewRoom(Room room) throws JTHDataBaseException {
        return dataAccess.submitNewRoom(room);
    }

    @Override
    public void removeRoom(int roomId) throws JTHDataBaseException {
        dataAccess.removeRoom(roomId);
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
