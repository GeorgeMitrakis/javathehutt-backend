package back.api;

import back.exceptions.JTHDataBaseException;
import back.exceptions.JTHInputException;
import back.conf.Configuration;
import back.data.RoomsDAO;
import back.model.Location;
import back.model.Room;
import back.model.User;
import back.util.JWT;
import back.util.JsonMapRepresentation;
import org.restlet.data.Form;
import org.restlet.representation.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RoomsResource extends ServerResource  {

    private final RoomsDAO roomsDAO = Configuration.getInstance().getRoomsDAO();


    @Override
    protected Representation get() throws ResourceException {
        String roomIdStr = getQueryValue("roomId");
        if (roomIdStr == null){
            String providerIdStr = getQueryValue("providerId");

            if (providerIdStr == null || providerIdStr.equals("")){
                return JsonMapRepresentation.result(false, "Get room: Missing or empty parameter(s)", null);
            }

            long providerId;
            try {
                providerId = Long.parseLong(providerIdStr);
            } catch (ArithmeticException e){
                return JsonMapRepresentation.result(false, "Get rooms: arithmetic parameter(s) given not a number", null);
            }

            List<Room> rooms;
            try {
                rooms = roomsDAO.getRoomsForProvider(providerId);
            } catch (JTHDataBaseException e) {
                return JsonMapRepresentation.result(false, "Get rooms: database error (null)", null);
            }

            Map<String, Object> m = new HashMap<>();
            m.put("rooms", rooms);
            return JsonMapRepresentation.result(true, "success", m);
        } else {
            if (roomIdStr.equals("")){
                return JsonMapRepresentation.result(false, "Get room: empty \"roomId\" parameter", null);
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
        String maxOccupantsStr = form.getFirstValue("maxOccupants");
        if (description == null) description = "";

        if (providerIdStr == null || providerIdStr.equals("") ||
            priceStr == null || priceStr.equals("") ||
            capacityStr == null || capacityStr.equals("") ||
            cordXStr == null || cordXStr.equals("") ||
            cordYStr == null || cordYStr.equals("") ||
            cityName == null || cityName.equals("") ||
            roomName == null || roomName.equals("") ||
            maxOccupantsStr == null || maxOccupantsStr.equals("")
        ){
            return JsonMapRepresentation.result(false, "Post room: Missing or empty parameter(s)", null);
        }

        long providerId;
        int capacity, maxOccupants;
        double price, cordX, cordY;
        try {
            providerId = Long.parseLong(providerIdStr);
            price = Double.parseDouble(priceStr);
            cordX = Double.parseDouble(cordXStr);
            cordY = Double.parseDouble(cordYStr);
            capacity = Integer.parseInt(capacityStr);
            maxOccupants = Integer.parseInt(maxOccupantsStr);
        } catch (ArithmeticException e){
            return JsonMapRepresentation.result(false, "Post room: arithmetic parameter(s) given not a number", null);
        }

        if (Configuration.CHECK_AUTHORISATION) {
            try {
                String jwt = JWT.getJWTFromHeaders(getRequest());
                if (!JWT.assertRole(jwt, "provider")){
                    return JsonMapRepresentation.result(false,"Post room: forbidden (not a provider)",null);
                } else if (!JWT.assertUser(jwt, providerId)) {
                    return JsonMapRepresentation.result(false,"Post room: forbidden (not allowed to submit a room for another provider)",null);
                }
            } catch (JTHInputException e){
                return JsonMapRepresentation.result(false,"Post room: " + e.getErrorMsg(),null);
            }
        }

        Location location = new Location(cityName, cordX, cordY);
        Room room = new Room(-1, roomName, providerId, -1, price, capacity, "true".equals(wifi), "true".equals(pool), "true".equals(shauna), location, description, maxOccupants, false);

        int roomId;
        try {
            roomId = roomsDAO.submitNewRoom(room);
        } catch (JTHDataBaseException e){
            return JsonMapRepresentation.result(false, "Post room: DataBase error (or invalid providerId given)", null);
        }

        room.setId(roomId);
        room.fetchProvider();  // so that it's there in JSon return value

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

        Room room;
        try {
            room = roomsDAO.getRoomById(roomId);
        } catch (JTHDataBaseException e) {
            return JsonMapRepresentation.result(false, "Delete room: DataBase error", null);
        }
        if (room == null){
            return JsonMapRepresentation.result(false, "Delete room: room does not exist", null);
        }

        // only a owner-provider and an admin should be allowed to delete a room
        if (Configuration.CHECK_AUTHORISATION) {
            try {
                String jwt = JWT.getJWTFromHeaders(getRequest());
                User user = JWT.getUserJWT(jwt);
                if (!JWT.assertRole(jwt, "admin") && (user == null || room.getProviderId() != user.getId())) {
                    return JsonMapRepresentation.result(false, "Delete room: forbidden (not allowed to delete a room for another provider unless admin)", null);
                }
            } catch (JTHInputException e){
                return JsonMapRepresentation.result(false,"Delete room: " + e.getErrorMsg(),null);
            }
        }

        try {
            roomsDAO.removeRoom(roomId);
        } catch (JTHDataBaseException e){
            return JsonMapRepresentation.result(false, "Delete room: DataBase error", null);
        }

        return JsonMapRepresentation.result(true, "success", null);
    }

    @Override
    protected Representation put(Representation entity) throws ResourceException {
        Form form = new Form(entity);
        String roomIdStr = form.getFirstValue("roomId");
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
        String maxOccupantsStr = form.getFirstValue("maxOccupants");

        if (roomIdStr == null || roomIdStr.equals("") ||
            priceStr == null || priceStr.equals("") ||
            capacityStr == null || capacityStr.equals("") ||
            cordXStr == null || cordXStr.equals("") ||
            cordYStr == null || cordYStr.equals("") ||
            cityName == null || cityName.equals("") ||
            roomName == null || roomName.equals("") ||
            maxOccupantsStr == null || maxOccupantsStr.equals("")
        ){    // must demand them here as well or update becomes really tricky
            return JsonMapRepresentation.result(false, "Put room: Missing or empty parameter(s)", null);
        }


        int roomId;
        int capacity, maxOccupants;
        double price, cordX, cordY;
        try {
            roomId = Integer.parseInt(roomIdStr);
            price = Double.parseDouble(priceStr);
            cordX = Double.parseDouble(cordXStr);
            cordY = Double.parseDouble(cordYStr);
            capacity = Integer.parseInt(capacityStr);
            maxOccupants = Integer.parseInt(maxOccupantsStr);
        } catch (ArithmeticException e){
            return JsonMapRepresentation.result(false, "Put room: arithmetic parameter(s) given not a number", null);
        }

        if (Configuration.CHECK_AUTHORISATION) {
            try {
                String jwt = JWT.getJWTFromHeaders(getRequest());
                if (!JWT.assertRole(jwt, "provider")){
                    return JsonMapRepresentation.result(false,"Put room: forbidden (not a provider)",null);
                } else if (!JWT.assertUser(jwt, roomId)) {
                    return JsonMapRepresentation.result(false,"Put room: forbidden (not allowed to submit a room for another provider)",null);
                }
            } catch (JTHInputException e){
                return JsonMapRepresentation.result(false,"Put room: " + e.getErrorMsg(),null);
            }
        }

        Location location = new Location(cityName, cordX, cordY);

        Room oldRoom;
        try {
            oldRoom = roomsDAO.getRoomById(roomId);
        } catch (JTHDataBaseException e) {
            return JsonMapRepresentation.result(false, "Put room: DataBase error (or invalid roomId given)", null);
        }

        // restore defaults if not provided from old room
        if (description == null) description = oldRoom.getDescription();
        boolean _wifi = (wifi == null) ? oldRoom.getWifi() : "true".equals(wifi) ;
        boolean _pool = (wifi == null) ? oldRoom.getPool() : "true".equals(pool) ;
        boolean _shauna = (wifi == null) ? oldRoom.getShauna() : "true".equals(shauna) ;

        Room room = new Room(roomId, roomName, oldRoom.getProviderId(), oldRoom.getLocationId(), price, capacity, _wifi, _pool, _shauna, location, description, maxOccupants, true);

        try {
            roomsDAO.updateRoom(room);
        } catch (JTHDataBaseException e){
            return JsonMapRepresentation.result(false, "Put room: DataBase error (or invalid roomId given)", null);
        }

        Map<String, Object> m = new HashMap<>();
        m.put("room", room);
        return JsonMapRepresentation.result(true, "success", m);
    }

}
