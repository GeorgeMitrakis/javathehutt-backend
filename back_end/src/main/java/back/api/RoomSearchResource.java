package back.api;

import back.Exceptions.JTHDataBaseException;
import back.conf.Configuration;
import back.data.RoomsDAO;
import back.data.UserDAO;
import back.model.Room;
import back.model.SearchConstraints;
import org.restlet.representation.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class RoomSearchResource extends ServerResource {

    private final UserDAO userDAO = Configuration.getInstance().getUserDAO();
    private final RoomsDAO roomsDAO = Configuration.getInstance().getRoomsDAO();


    @Override
    protected Representation get() throws ResourceException {

        SearchConstraints constraints = new SearchConstraints();

        //Read the parameters
        String minPriceStr = getQueryValue("minPrice");
        String maxPriceStr = getQueryValue("maxPrice");
        String maxDist = getQueryValue("maxDist");
        String hasPool = getQueryValue("hasPool");
        String hasWifi = getQueryValue("hasWifi");
        String hasShauna = getQueryValue("hasShauna");
        // todo:readlocation

        try {
            constraints.setMaxCost(Integer.parseInt(maxPriceStr));
            constraints.setMinCost(Integer.parseInt(minPriceStr));
            constraints.setShauna(hasPool.equals("true"));
            constraints.setPool(hasWifi.equals("true"));
            constraints.setWifi(hasShauna.equals("true"));
            constraints.setRange(Integer.parseInt(maxDist));
        } catch (NumberFormatException e){
            return JsonMapRepresentation.result(false, "Search error: non number sent for a number parameter", null);
        }
        // todo: setlocation

        // search based on those constraints
        List<Room> results;
        try {
             results = roomsDAO.searchRooms(constraints);
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
