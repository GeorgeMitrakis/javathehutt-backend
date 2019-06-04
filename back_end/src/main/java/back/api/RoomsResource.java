package back.api;

import back.Exceptions.JTHDataBaseException;
import back.conf.Configuration;
import back.data.RoomsDAO;
import back.model.Location;
import back.model.Room;
import back.util.JsonMapRepresentation;
import org.restlet.data.Form;
import org.restlet.representation.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import java.util.HashMap;
import java.util.Map;

public class RoomsResource extends ServerResource  {

    private final RoomsDAO roomsDAO = Configuration.getInstance().getRoomsDAO();


    @Override
    protected Representation get() throws ResourceException {
        String roomIdStr = getQueryValue("roomId");
        if (roomIdStr == null || roomIdStr.equals("")){
            return JsonMapRepresentation.result(false, "Get room: Missing or empty \"roomId\" parameter", null);
        }

        int roomId;
        try {
            roomId = Integer.parseInt(roomIdStr);
        } catch (ArithmeticException e){
            return JsonMapRepresentation.result(false, "Get room: \"roomId\" parameter given is not a number", null);
        }

        Room room;
        try {
            room = roomsDAO.getRoomById(roomId);
        } catch (JTHDataBaseException e){
            return JsonMapRepresentation.result(false, "Get room: DataBase error", null);
        }

        if (room == null){
            return JsonMapRepresentation.result(false, "Get room: There is no room with given id", null);
        }

        Map<String, Object> m = new HashMap<>();
        m.put("room", room);
        return JsonMapRepresentation.result(true, "success", m);
    }


    @Override
    protected Representation post(Representation entity) throws ResourceException {

        Form form = new Form(entity);

        String providerIdStr = form.getFirstValue("providerId");
        String priceStr = form.getFirstValue("price");
        String capacityStr = form.getFirstValue("capacity");
        String wifi = form.getFirstValue("wifi");
        String pool = form.getFirstValue("pool");
        String shauna = form.getFirstValue("shauna");
        String cordXStr = form.getFirstValue("cordX");
        String cordYStr = form.getFirstValue("cordY");
        String cityName = form.getFirstValue("cityName");
        String description = form.getFirstValue("description");
        String roomName = form.getFirstValue("roomName");
        if (description == null) description = "";

        if (providerIdStr == null || providerIdStr.equals("") ||
            priceStr == null || priceStr.equals("") ||
            capacityStr == null || capacityStr.equals("") ||
            cordXStr == null || cordXStr.equals("") ||
            cordYStr == null || cordYStr.equals("") ||
            cityName == null || cityName.equals("") ||
            roomName == null || roomName.equals("")
        ){
            return JsonMapRepresentation.result(false, "Post room: Missing or empty parameter(s)", null);
        }

        int providerId, capacity;
        double price, cordX, cordY;
        try {
            providerId = Integer.parseInt(providerIdStr);
            price = Double.parseDouble(priceStr);
            cordX = Double.parseDouble(cordXStr);
            cordY = Double.parseDouble(cordYStr);
            capacity = Integer.parseInt(capacityStr);
        } catch (ArithmeticException e){
            return JsonMapRepresentation.result(false, "Post room: arithmetic parameter(s) given not a number", null);
        }

        Location location = new Location(cityName, cordX, cordY);
        Room room = new Room(-1, roomName, providerId, price, capacity, "true".equals(wifi), "true".equals(pool), "true".equals(shauna), location, description);

        try {
            roomsDAO.submitNewRoom(room);
        } catch (JTHDataBaseException e){
            return JsonMapRepresentation.result(false, "Post room: DataBase error (or invalid providerId given)", null);
        }

        Map<String, Object> m = new HashMap<>();
        m.put("room", room);
        return JsonMapRepresentation.result(true, "success", m);
    }

    @Override
    protected Representation delete() throws ResourceException {

        String roomIdStr = getQueryValue("roomId");
        if (roomIdStr == null || roomIdStr.equals("")){
            return JsonMapRepresentation.result(false, "Delete room: Missing or empty \"roomId\" parameter", null);
        }

        int roomId;
        try {
            roomId = Integer.parseInt(roomIdStr);
        } catch (ArithmeticException e){
            return JsonMapRepresentation.result(false, "Delete room: \"roomId\" parameter given is not a number", null);
        }

        try {
            roomsDAO.removeRoom(roomId);
        } catch (JTHDataBaseException e){
            return JsonMapRepresentation.result(false, "Delete room: DataBase error", null);
        }

        return JsonMapRepresentation.result(true, "success", null);
    }

}
