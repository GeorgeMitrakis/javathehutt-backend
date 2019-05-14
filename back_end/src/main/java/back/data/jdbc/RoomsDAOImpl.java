package back.data.jdbc;

import back.Exceptions.JTHDataBaseException;
import back.data.RoomsDAO;
import back.model.Room;

public class RoomsDAOImpl implements RoomsDAO {

    private final DataAccess dataAccess;

    public RoomsDAOImpl(DataAccess dataAccess) throws JTHDataBaseException {
        this.dataAccess = dataAccess;
    }

    @Override
    public Room getRoomById(int roomId) throws JTHDataBaseException {
        return dataAccess.getRoom(roomId);
    }
}
