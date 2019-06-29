package back.data;

import back.model.Room;
import back.model.SearchConstraints;
import back.model.Transaction;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.common.unit.DistanceUnit;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;


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
                    .addTransportAddress(new TransportAddress(InetAddress.getByName("localhost"), 9300));
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
            e.printStackTrace();
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
        List<Map> res = new LinkedList<>();


        BoolQueryBuilder B = QueryBuilders.boolQuery();
        if(constraints.getWifi()){
            B = B.must(QueryBuilders.matchQuery("wifi",true));
        }

        if(constraints.getShauna()){
            B = B.must(QueryBuilders.matchQuery("shauna",true));
        }

        if(constraints.getShauna()){
            B = B.must(QueryBuilders.matchQuery("pool",true));
        }

        if(constraints.hasMinCost()){
            B = B.must(QueryBuilders.rangeQuery("cost").gte(constraints.getMinCost()));
        }

        if(constraints.hasMaxCost()){
            B = B.must(QueryBuilders.rangeQuery("cost").lte(constraints.getMinCost()));
        }

        if(constraints.hasRange()){
            B = B.must(QueryBuilders.geoDistanceQuery("location").distance(constraints.getRange(), DistanceUnit.KILOMETERS));
        }



        SearchResponse response = client.prepareSearch("testindex")
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                .setQuery(B)
                .setFrom(0/*offset*/).setSize(10/*limit*/).setExplain(true)
                .get();
        for(SearchHit h : response.getHits().getHits()){
            res.add(h.getSourceAsMap());
        }
        System.out.println(res);
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
