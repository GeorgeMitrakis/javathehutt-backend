package back.data.jdbc;

import back.Exceptions.JTHDataBaseException;
import back.data.RoomsDAO;
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

}
