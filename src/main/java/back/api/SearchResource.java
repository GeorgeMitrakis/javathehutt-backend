package back.api;

import back.exceptions.JTHDataBaseException;
import back.conf.Configuration;
import back.data.RoomsDAO;
import back.model.Room;
import back.model.SearchConstraints;
import back.model.SearchConstraintsBuilder;
import back.util.JsonMapRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SearchResource extends ServerResource {

    private final RoomsDAO roomsDAO = Configuration.getInstance().getRoomsDAO();


    @Override
    protected Representation get() throws ResourceException {

        // Read the parameters
        String minPriceStr = getQueryValue("minPrice");
        String maxPriceStr = getQueryValue("maxPrice");
        String maxDist = getQueryValue("maxDist");
        String hasPool = getQueryValue("hasPool");
        String hasWifi = getQueryValue("hasWifi");
        String hasBreakfast = getQueryValue("hasBreakfast");
        String hasShauna = getQueryValue("hasShauna");
        String cityName = getQueryValue("cityName");
        String pointX = getQueryValue("pointX");
        String pointY = getQueryValue("pointY");
        String occupants = getQueryValue("occupants");
        String description = getQueryValue("description");
        String startDate = getQueryValue("startDate");
        String endDate = getQueryValue("endDate");

        SearchConstraints constraints;
        try {
            constraints = new SearchConstraintsBuilder()
                    .setMinCost((minPriceStr == null) ? null : Integer.parseInt(minPriceStr))
                    .setMaxCost((maxPriceStr == null) ? null : Integer.parseInt(maxPriceStr))
                    .setRange((maxDist == null) ? null : Double.parseDouble(maxDist))
                    .setWifi((hasWifi == null) ? null : "true".equals(hasWifi))
                    .setPool((hasPool == null) ? null : "true".equals(hasPool))
                    .setShauna((hasShauna == null) ? null : "true".equals(hasShauna))
                    .setBreakfast((hasBreakfast == null) ? null : "true".equals(hasBreakfast))
                    .setLocation(cityName, (pointX == null) ? null : Double.parseDouble(pointX), (pointY == null) ? null : Double.parseDouble(pointY))
                    .setOccupants((occupants == null) ? 1 : Integer.parseInt(occupants))
                    .setDescription(description)
                    .setStartDate(startDate)
                    .setEndDate(endDate)
                    .build();
        } catch (NumberFormatException e){
            e.printStackTrace();
            return JsonMapRepresentation.result(false, "Search error: non number sent for a number parameter", null);
        }

        String limitStr = getQueryValue("limit");
        String offsetStr = getQueryValue("offset");
        int limit = 100, offset = 0;   // default
        if (limitStr != null)  {
            try { limit = Integer.parseInt(limitStr); } catch (NumberFormatException e) { return JsonMapRepresentation.result(false, "Search error: non number sent for a number parameter", null); }
        }
        if (offsetStr != null) {
            try { offset = Integer.parseInt(offsetStr); } catch (NumberFormatException e) { return JsonMapRepresentation.result(false, "Search error: non number sent for a number parameter", null); }
        }

        // search based on those constraints
        List<Room> results;
        try {
             results = roomsDAO.searchRooms(constraints, limit, offset);
        } catch (JTHDataBaseException e){
            return JsonMapRepresentation.result(false, "Search error: database error", null);
        }
        if (results != null){
            Map<String, Object> m = new HashMap<>();
            m.put("results", results);
            return JsonMapRepresentation.result(true, null, m);
        } else return JsonMapRepresentation.forSimpleResult("Oopsies: null search return");  // should not happen
    }

}
