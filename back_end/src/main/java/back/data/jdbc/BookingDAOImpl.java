package back.data.jdbc;

import back.data.BookingDAO;

public class BookingDAOImpl implements BookingDAO {

    private final DataAccess dataAccess;

    public BookingDAOImpl(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    // TODO

}
