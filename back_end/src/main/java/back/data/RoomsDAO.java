package back.data;

import back.model.Room;

public interface RoomsDAO {

    // Interface for accessing Room's part of the database

    Room getRoomById(int roomId);

}