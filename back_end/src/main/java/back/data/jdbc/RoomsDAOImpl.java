package back.data.jdbc;

import back.data.RoomsDAO;
import back.model.Room;

public class RoomsDAOImpl implements RoomsDAO {

    private final DataAccess dataAccess;

    public RoomsDAOImpl(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    @Override
    public Room getRoomById(int roomId) {
        return dataAccess.getRoom(roomId);
    }
}
