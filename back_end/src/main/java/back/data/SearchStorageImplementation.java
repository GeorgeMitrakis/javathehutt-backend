package back.data;

import back.exceptions.JTHDataBaseException;
import back.model.Room;
import back.model.SearchConstraints;
import back.model.Transaction;

import java.io.IOException;
import java.util.*;

import org.apache.http.HttpHost;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.geo.GeoPoint;
import org.elasticsearch.common.unit.DistanceUnit;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;


public class SearchStorageImplementation implements SearchStorageAPI {

/*
    NoSQL document structure:

    { roomId, providerId, price, capacity, cityName, cordX, cordY, transactions: [ { transactionId, visitorId, startDate, endDate } , ... ], wifi, pool, shauna }

    example: { "roomId" : 1, "providerId": 1, "price": 20, "capacity": 72, "cityName": "Athens", "cordX" : -1.0, "cordY": -1.0, "transactions": [ { "transactionId": 1, "visitorId": 3, "startDate": null, "endDate": null } ], "wifi": true, "pool": false, "shauna": false }

 */

    private RestHighLevelClient client;

    public SearchStorageImplementation(){
    }

    public void setup(String eshost, int port, String roomIndexName) throws Exception {

        client = new RestHighLevelClient(RestClient.builder(
                new HttpHost(eshost, port, "http")));

    }


    @Override
    public void pushRoom(Room room, List<Transaction> transactions) throws JTHDataBaseException {
        try {
            IndexRequest request = new IndexRequest("jth_rooms").type("room").id(Integer.toString(room.getId())).source(RoomToXContent(room));
            client.index(request, RequestOptions.DEFAULT);
        } catch (Exception e){
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
            .field("breakfast", room.getBreakfast())
            .field("description", room.getDescription())
            .field("capacity", room.getCapacity())
            .field("transactions", new ArrayList<>())
            .field("roomName", room.getRoomName())
            .field("location",new GeoPoint(room.getLocation().getCordX(), room.getLocation().getCordY()))
            .field("providerId",room.getProviderId())
            .field("locationId",room.getLocationId())
            .field("maxOccupants", room.getMaxOccupants())
            .field("cityName", room.getLocation().getCityname())
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
    public void pushTransaction(int roomId, Transaction transaction) throws JTHDataBaseException{
        try {
            XContentBuilder t = TransactionToXContent(transaction);
        } catch (IOException e) {
            throw new JTHDataBaseException();
        }
        //UpdateResponse res = client.prepareUpdate(roomIndexName, "room", Integer.toString(roomId)).setScript(new Script("").getParams().pu)


    }

    @Override
    public void deleteRoom(int roomId) throws JTHDataBaseException {
        try {
            client.delete(new DeleteRequest("jth_rooms").id(Integer.toString(roomId)), RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
            throw new JTHDataBaseException();
        }
    }

    @Override
    public List<Room> searchRooms(SearchConstraints constraints, int limit, int offset) {
        try {
            List<Room> res = new LinkedList<>();
            BoolQueryBuilder B = QueryBuilders.boolQuery();

            if (constraints.getWifi()) {
                B = B.must(QueryBuilders.matchQuery("wifi", true));
            }

            if (constraints.getShauna()) {
                B = B.must(QueryBuilders.matchQuery("shauna", true));
            }

            if (constraints.getPool()) {
                B = B.must(QueryBuilders.matchQuery("pool", true));
            }

            if (constraints.getBreakfast()) {
                B = B.must(QueryBuilders.matchQuery("breakfast", true));
            }

            if (constraints.hasMinCost()) {
                B = B.must(QueryBuilders.rangeQuery("price").gte(constraints.getMinCost()));
            }

            if (constraints.hasMaxCost()) {

                B = B.must(QueryBuilders.rangeQuery("price").lte(constraints.getMaxCost()));
            }


            if (constraints.hasRange()) {

                B = B.must(QueryBuilders.geoDistanceQuery("location")
                        .point(constraints.getLocation().getCordX(),constraints.getLocation().getCordY()).distance(constraints.getRange(), DistanceUnit.KILOMETERS));
            }


            if (constraints.hasDescription()){
                B = B.must(QueryBuilders.matchQuery("description", constraints.getDescription()).fuzziness(Fuzziness.AUTO));
            }

            B = B.must(QueryBuilders.rangeQuery("maxOccupants").gte(constraints.getOccupants()));



            SearchRequest request = new SearchRequest("jth_rooms").source(
                    new SearchSourceBuilder().from(offset).size(limit).query(B)
            );
            SearchResponse response = client.search(request, RequestOptions.DEFAULT);

            for (SearchHit h : response.getHits().getHits()) {
                res.add(Room.fromMap(h.getSourceAsMap()));
            }
            //System.out.println(res);
            return res;
        } catch (Exception e){
            e.printStackTrace();
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
