package back.data;

import back.exceptions.JTHDataBaseException;
import back.model.Room;
import back.model.SearchConstraints;
import back.model.Transaction;

import java.io.IOException;
import java.net.InetAddress;
import java.util.*;

import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.geo.GeoPoint;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.common.unit.DistanceUnit;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.script.Script;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;


public class SearchStorageImplementation implements SearchStorageAPI {

/*
    NoSQL document structure:

    { roomId, providerId, price, capacity, cityName, cordX, cordY, transactions: [ { transactionId, visitorId, startDate, endDate } , ... ], wifi, pool, shauna }

    example: { "roomId" : 1, "providerId": 1, "price": 20, "capacity": 72, "cityName": "Athens", "cordX" : -1.0, "cordY": -1.0, "transactions": [ { "transactionId": 1, "visitorId": 3, "startDate": null, "endDate": null } ], "wifi": true, "pool": false, "shauna": false }

 */

    private TransportClient client;
    private String roomIndexName;

    public SearchStorageImplementation(){
    }

    public void setup(String eshost, int port, String roomIndexName) throws Exception {
        client = new PreBuiltTransportClient(Settings.EMPTY)
                .addTransportAddress(new TransportAddress(InetAddress.getByName(eshost), port));
    }


    @Override
    public void pushRoom(Room room, List<Transaction> transactions) throws JTHDataBaseException {
        try {
            String roomId = String.format("%d", room.getId());
            XContentBuilder builder = RoomToXContent(room);
            IndexResponse response = client.prepareIndex(roomIndexName, "room", roomId).setSource(builder).get();
        }catch(Exception e){
            e.printStackTrace();
            throw new JTHDataBaseException();
        }

    }

    private XContentBuilder RoomToXContent(Room room) throws IOException {
        return jsonBuilder().startObject()
            .field("id", room.getId())
            .field("price", room.getPrice())
            .field("wifi",room.getWifi())
            .field("shauna", room.getShauna())
            .field("description", room.getDescription())
            .field("capacity", room.getCapacity())
            .field("transactions", new ArrayList<>())
            .field("name", room.getRoomName())
            .field("location",new GeoPoint(room.getLocation().getCordX(), room.getLocation().getCordY()))
            .endObject()

            ;

    }

    private XContentBuilder TransactionToXContent(Transaction transaction) throws IOException {
        return jsonBuilder().startObject()
            .field("start_date", transaction.getStartDate())
            .field("end_date", transaction.getEndDate())
                .endObject()
        ;
    }

    @Override
    public void pushTransaction(int roomId, Transaction transaction) throws Exception{
        XContentBuilder t = TransactionToXContent(transaction);
        //UpdateResponse res = client.prepareUpdate(roomIndexName, "room", Integer.toString(roomId)).setScript(new Script("").getParams().pu)


    }

    @Override
    public void deleteRoom(int roomId) {
        String roomIdStr = String.format("%d",roomId);
        DeleteResponse response = client.prepareDelete(roomIndexName, "room", roomIdStr).get();
    }

    @Override
    public List<Room> searchRooms(SearchConstraints constraints, int limit, int offset) {
        try {
            List<Map> res = new LinkedList<>();
            BoolQueryBuilder B = QueryBuilders.boolQuery();

            if (constraints.getWifi()) {
                B = B.must(QueryBuilders.matchQuery("wifi", true));
            }

            if (constraints.getShauna()) {
                B = B.must(QueryBuilders.matchQuery("shauna", true));
            }

            if (constraints.getShauna()) {
                B = B.must(QueryBuilders.matchQuery("pool", true));
            }

            if (constraints.hasMinCost()) {
                B = B.must(QueryBuilders.rangeQuery("price").gte(constraints.getMinCost()));
            }

            if (constraints.hasMaxCost()) {
                B = B.must(QueryBuilders.rangeQuery("price").lte(constraints.getMinCost()));
            }

            if (constraints.hasRange()) {
                B = B.must(QueryBuilders.geoDistanceQuery("location").distance(constraints.getRange(), DistanceUnit.KILOMETERS));
            }

            if (constraints.hasDescription()){
                B = B.should(QueryBuilders.fuzzyQuery("description", constraints.getDescription()));
            }

            B = B.must(QueryBuilders.rangeQuery("max_occupants").gte(constraints.getOccupants()));

            SearchResponse response = client.prepareSearch(roomIndexName)
                    .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                    .setQuery(B)
                    .setFrom(offset).setSize(limit).setExplain(true)
                    .get();
            for (SearchHit h : response.getHits().getHits()) {
                res.add(h.getSourceAsMap());
                res.add(Room.fromMap(h.getSourceAsMap()));
            }
            System.out.println(res);
        } catch (Exception e){
            System.out.println("Tried elastic search and failed");
        }
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
