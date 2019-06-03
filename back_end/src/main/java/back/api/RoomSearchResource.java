package back.api;

import back.Exceptions.JTHDataBaseException;
import back.conf.Configuration;
import back.data.RoomsDAO;
import back.data.UserDAO;
import back.model.Room;
import back.model.SearchConstraints;
import back.util.JsonMapRepresentation;
import org.restlet.data.Form;
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
    protected Representation post(Representation entity) throws ResourceException {

        SearchConstraints constraints = new SearchConstraints();
        Form form = new Form(entity);

        //Read the parameters
        String minPriceStr = form.getFirstValue("minPrice");
        String maxPriceStr = form.getFirstValue("maxPrice");
        String maxDist = form.getFirstValue("maxDist");
        String hasPool = form.getFirstValue("hasPool");
        String hasWifi = form.getFirstValue("hasWifi");
        String hasShauna = form.getFirstValue("hasShauna");
        String cityName = form.getFirstValue("Name");
        String pointX = form.getFirstValue("pointX");
        String pointY = form.getFirstValue("pointY");

        try {
            if (maxPriceStr != null) constraints.setMaxCost(Integer.parseInt(maxPriceStr));
            if (minPriceStr != null) constraints.setMinCost(Integer.parseInt(minPriceStr));
            if (hasWifi != null) constraints.setWifi(hasWifi.equals("true"));
            if (hasPool != null) constraints.setPool(hasPool.equals("true"));
            if (hasShauna != null) constraints.setShauna(hasShauna.equals("true"));
            if (maxDist != null) constraints.setRange(Integer.parseInt(maxDist));
            if (pointX != null && pointY != null && cityName != null) constraints.setLocation(cityName, Double.parseDouble(pointX), Double.parseDouble(pointY));
        } catch (NumberFormatException e){
            e.printStackTrace();
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
