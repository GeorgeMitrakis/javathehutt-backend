package back.data;

import back.exceptions.JTHDataBaseException;
import back.model.Provider;
import back.model.Room;
import back.model.SearchConstraints;
import back.model.Transaction;

import java.io.IOException;
import java.util.*;

import org.apache.http.HttpHost;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.delete.DeleteRequest;
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
import org.elasticsearch.search.builder.SearchSourceBuilder;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;


public class SearchStorageImplementation implements SearchStorageAPI {

/*
    NoSQL document structure:

    { id, providerId, roomName, description, price, capacity, cityName, location, maxOccupants, transactions: [ { start_date, end_date } , ... ], wifi, pool, shauna, breakfast }

 */

    private RestHighLevelClient client;


    public void setup(String eshost, int port, String roomIndexName) throws Exception {
        client = new RestHighLevelClient(RestClient.builder(new HttpHost(eshost, port, "http")));
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

    @Override
    public void pushTransaction(Room room, Transaction transaction) throws JTHDataBaseException {
        // This is a lazy implementation of pushTransaction... it could be better.
        pushRoom(room, room.getTransactions());
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

            if (constraints.getPool()) {
                B = B.must(QueryBuilders.matchQuery("pool", true));
            }

            if (constraints.getShauna()) {
                B = B.must(QueryBuilders.matchQuery("shauna", true));
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

            B = B.must(QueryBuilders.rangeQuery("maxOccupants").gte(constraints.getOccupants()));

            if (constraints.hasCityName()){
                B = B.must(QueryBuilders.matchQuery("cityName", constraints.getLocation().getCityname()).fuzziness(Fuzziness.AUTO));
            }

            if (constraints.hasDescription()){
                B = B.must(QueryBuilders.matchQuery("description", constraints.getDescription()).fuzziness(Fuzziness.AUTO));
            }

            if (constraints.hasRange()) {
                B = B.must(QueryBuilders.geoDistanceQuery("location")
                     .point(constraints.getLocation().getCordX(),constraints.getLocation().getCordY()).distance(constraints.getRange(), DistanceUnit.KILOMETERS));
            }

            if (constraints.hasDateConstraints()) {
                B = availabilityQuery(B, constraints.getStartDate(), constraints.getEndDate());
            }

            SearchRequest request = new SearchRequest("jth_rooms").source(new SearchSourceBuilder().from(offset).size(limit).query(B));
            SearchResponse response = client.search(request, RequestOptions.DEFAULT);

            for (SearchHit h : response.getHits().getHits()) {
                res.add(Room.fromMap(h.getSourceAsMap()));
            }
            return res;
        } catch (Exception e){
            e.printStackTrace();
            System.out.println("Tried elastic search and failed");
            return null;
        }
    }

    private XContentBuilder RoomToXContent(Room room) throws IOException, JTHDataBaseException {
        Provider provider = room.fetchProvider();
        if (provider == null) throw new JTHDataBaseException();
        XContentBuilder res = jsonBuilder().startObject()
                .field("id", room.getId())
                .field("providerId", room.getProviderId())
                .field("roomName", room.getRoomName())
                .field("description", room.getDescription())
                .field("price", room.getPrice())
                .field("capacity", room.getCapacity())
                .field("cityName", room.getLocation().getCityname())
                .field("location", new GeoPoint(room.getLocation().getCordX(), room.getLocation().getCordY()))
                .field("locationId", room.getLocationId())
                .field("wifi", room.getWifi())
                .field("pool", room.getPool())
                .field("shauna", room.getShauna())
                .field("breakfast", room.getBreakfast())
                .field("maxOccupants", room.getMaxOccupants())
                .field("email", provider.getEmail())
                .field("isBanned", provider.isBanned())
                .field("providerName", provider.getProvidername());
        res = addTransactions(res,room.getTransactions());
        res = res.endObject();
        return res;

    }

    private XContentBuilder addTransactions(XContentBuilder res,List<Transaction> transactions) throws IOException {
        res = res.startArray("transactions");
        for(Transaction t: transactions){
            res = res.startObject()
                     .field("start_date", t.getStartDate())
                     .field("end_date", t.getEndDate())
                     .endObject();
        }
        res = res.endArray();
        return res;
    }


    private XContentBuilder TransactionToXContent(Transaction transaction) throws IOException {
        return jsonBuilder().startObject()
                .field("startDate", transaction.getStartDate())
                .field("endDate", transaction.getEndDate())
                .endObject();
    }


    private BoolQueryBuilder availabilityQuery(BoolQueryBuilder b, String start, String end){
        return QueryBuilders.boolQuery().mustNot(
                QueryBuilders.nestedQuery("transactions",
                        QueryBuilders.boolQuery()
                        .should(QueryBuilders.rangeQuery("transactions.start_date").gte(start).lt(end))
                        .should(QueryBuilders.rangeQuery("transactions.end_date").gte(start).lt(end))
                        .should(QueryBuilders
                                .boolQuery()
                                .must(QueryBuilders.rangeQuery("transactions.start_date").lt(start))
                                .must(QueryBuilders.rangeQuery("transactions.end_date").gt(end))
                        ), ScoreMode.Avg));
    }

}
