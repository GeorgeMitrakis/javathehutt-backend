package back.data.jdbc;

import back.Exceptions.JTHDataBaseException;
import back.data.RoomsDAO;
import back.model.Provider;
import back.model.Room;
import back.model.SearchConstraints;
import back.model.Visitor;

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
    public boolean addRatingToRoom(Visitor visitor, Room room, int stars, String comment) throws JTHDataBaseException {
        if (stars < 0 || stars > 5) return false;
        dataAccess.addRatingToRoom(visitor, room, stars, comment);
        return true;
    }

    @Override
    public void removeRatingFromRoom(int ratingId) throws JTHDataBaseException {
        dataAccess.removeRatingFromRoom(ratingId);
    }

    @Override
    public boolean submitNewRoom(Provider provider, Room room) throws JTHDataBaseException {
        return dataAccess.submitNewRoom(provider, room);
    }

    @Override
    public void removeRoom(Room room) throws JTHDataBaseException {
        dataAccess.removeRoom(room);
    }

    @Override
    public List<String> autocompletePrefix(String prefix) throws JTHDataBaseException {
        return dataAccess.autocompletePrefix(prefix);
    }

}
