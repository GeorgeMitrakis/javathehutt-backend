package back.data;

import back.data.jdbc.DataAccess;
import back.exceptions.JTHDataBaseException;
import back.conf.Configuration;
import back.model.Room;
import back.model.Transaction;
import back.model.User;

import java.util.List;

public class BookingDAOImpl implements BookingDAO {

    private final DataAccess dataAccess;
    private final SearchStorageImplementation search;


    public BookingDAOImpl(DataAccess dataAccess, SearchStorageImplementation search) {
        this.dataAccess = dataAccess;
        this.search = search;
    }

    @Override
    public boolean bookRoomForVisitor(User user, Room room, String sqlStartDate, String sqlEndDate, int occupants) throws JTHDataBaseException {
        boolean succ = dataAccess.insertTransaction(user, room, sqlStartDate, sqlEndDate, occupants);
        if (succ) search.pushTransaction(room.getId(), new Transaction(user.getId(), room.getId(), -1, sqlStartDate, sqlEndDate, -1.0, occupants));
        return succ;
    }

    @Override
    public boolean addRoomToFavourites(long visitorId, int roomId) throws JTHDataBaseException{
        return dataAccess.addRoomToFavourites(visitorId, roomId);
    }

    @Override
    public boolean removeRoomFromFavourites(long visitorId, int roomId) throws JTHDataBaseException {
        return dataAccess.removeRoomFromFavourites(visitorId, roomId);
    }

    @Override
    public List<Room> getFavouriteRoomIdsForVisitor(long visitorId) throws JTHDataBaseException {
        return dataAccess.getFavouriteRoomIdsForVisitor(visitorId);
    }

    @Override
    public List<Transaction> getTransactions() throws JTHDataBaseException {
        return dataAccess.getTransactions();
    }

    @Override
    public List<Transaction> getTransactionsForRoom(int roomId) throws JTHDataBaseException {
        return dataAccess.getTransactionsForRoom(roomId);
    }

    @Override
    public double calcSystemProfit() throws JTHDataBaseException {
        String ratio = Configuration.getInstance().getProperty("systemProfitRatio");
        if (ratio == null) { System.err.println("> Warning: Missing systemProfitRatio parameter"); return 0.0; }
        else return dataAccess.sumTransactionCosts() * Double.parseDouble(ratio);
    }
}
