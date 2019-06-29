package back.data;

import back.model.Room;
import back.model.SearchConstraints;
import back.model.Transaction;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;


public class SearchStorageImplementation implements SearchStorageAPI {

/*
    NoSQL document structure:

    { roomId, providerId, price, capacity, cityName, cordX, cordY, transactions: [ { transactionId, visitorId, startDate, endDate } , ... ], wifi, pool, shauna }

    example: { "roomId" : 1, "providerId": 1, "price": 20, "capacity": 72, "cityName": "Athens", "cordX" : -1.0, "cordY": -1.0, "transactions": [ { "transactionId": 1, "visitorId": 3, "startDate": null, "endDate": null } ], "wifi": true, "pool": false, "shauna": false }

 */

    private TransportClient client;


    public SearchStorageImplementation(){
        try {
            client = new PreBuiltTransportClient(Settings.EMPTY)
                    .addTransportAddress(new TransportAddress(InetAddress.getByName("localhost"), 9200));
        } catch (UnknownHostException e) {
            e.printStackTrace();
            client = null;
        }
        // on shutdown
        //client.close(); but no destructor in Java (TODO?)

    }

    @Override
    public void test(){
        System.out.println("\nTEST:");
        try {
            GetResponse response = client.prepareGet("rooms", "_doc", "room").get();
            System.out.println(response.getSourceAsString());
        } catch( Exception e){
            //e.printStackTrace();
            System.out.print("FAILED TEST (TODO FIX ELASTIC)");
        }
    }

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
