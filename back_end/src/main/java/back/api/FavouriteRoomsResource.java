package back.api;

import back.Exceptions.JTHDataBaseException;
import back.conf.Configuration;
import back.data.BookingDAO;
import back.model.Rating;
import back.model.Room;
import back.util.JWT;
import back.util.JsonMapRepresentation;
import org.restlet.data.Form;
import org.restlet.representation.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FavouriteRoomsResource extends ServerResource {

    private final BookingDAO bookingDAO = Configuration.getInstance().getBookingDAO();


    @Override
    protected Representation get() throws ResourceException {
        String visitorIdStr = getQueryValue("visitorId");
        if (visitorIdStr == null || visitorIdStr.equals("")){
            return JsonMapRepresentation.result(false, "Get ratings: Missing or empty parameter (room id)", null);
        }

        long visitorId;
        try {
            visitorId = Long.parseLong(visitorIdStr);
        } catch (ArithmeticException e){
            return JsonMapRepresentation.result(false, "Get ratings: room id given is not a number", null);
        }

        if (Configuration.CHECK_AUTHORISATION) {
            String jwt = getQueryValue("token");
            if (!JWT.assertRole(jwt, "admin") && !JWT.assertUser(jwt, visitorId)){
                return JsonMapRepresentation.result(false,"Post favourite room: forbidden (not allowed to see favourite rooms for another user unless admin)",null);
            }
        }

        List<Room> rooms;
        try {
            rooms = bookingDAO.getFavouriteRoomIdsForVisitor(visitorId);
        } catch (JTHDataBaseException e){
            return JsonMapRepresentation.result(false, "Get ratings: DataBase error (or invalid room id given)", null);
        }

        Map<String, Object> m = new HashMap<>();
        m.put("favourite_rooms", rooms);
        return JsonMapRepresentation.result(true, "success", m);
    }

    @Override
    protected Representation post(Representation entity) throws ResourceException {

        Form form = new Form(entity);

        String visitorIdStr = form.getFirstValue("visitorId");
        String roomIdStr = form.getFirstValue("roomId");
        if (roomIdStr == null || roomIdStr.equals("") || visitorIdStr == null || visitorIdStr.equals("")){
            return JsonMapRepresentation.result(false, "Post favourite room: Missing or empty parameter(s)", null);
        }

        long visitorId;
        int roomId;
        try {
            visitorId = Long.parseLong(visitorIdStr);
            roomId = Integer.parseInt(roomIdStr);
        } catch (ArithmeticException e){
            return JsonMapRepresentation.result(false, "Post favourite room: parameter(s) given is not a number", null);
        }

        if (Configuration.CHECK_AUTHORISATION) {
            String jwt = form.getFirstValue("token");
            if (!JWT.assertRole(jwt, "visitor")){
                return JsonMapRepresentation.result(false,"Post favourite room: forbidden (not a visitor)",null);
            } else if (!JWT.assertUser(jwt, visitorId)){
                return JsonMapRepresentation.result(false,"Post favourite room: forbidden (not allowed to add room to favourites for another user)",null);
            }
        }

        try {
            bookingDAO.addRoomToFavourites(visitorId, roomId);
        } catch (JTHDataBaseException e){
            return JsonMapRepresentation.result(false, "Post favourite room: DataBase error (or invalid ids given)", null);
        }

        return JsonMapRepresentation.result(true, "success", null);
    }


    @Override
    protected Representation delete() throws ResourceException {

        String visitorIdStr = getQueryValue("visitorId");
        String roomIdStr = getQueryValue("roomId");
        if (roomIdStr == null || roomIdStr.equals("") || visitorIdStr == null || visitorIdStr.equals("")){
            return JsonMapRepresentation.result(false, "Delete favourite room: Missing or empty parameter(s)", null);
        }

        long visitorId;
        int roomId;
        try {
            visitorId = Long.parseLong(visitorIdStr);
            roomId = Integer.parseInt(roomIdStr);
        } catch (ArithmeticException e){
            return JsonMapRepresentation.result(false, "Delete favourite room: parameter(s) given is not a number", null);
        }

        if (Configuration.CHECK_AUTHORISATION) {
            String jwt = getQueryValue("token");
            if (!JWT.assertRole(jwt, "admin") && !JWT.assertUser(jwt, visitorId)){
                return JsonMapRepresentation.result(false,"Delete favourite room: forbidden (not allowed to remove room from favourites for another user unless admin)",null);
            }
        }

        try {
            bookingDAO.removeRoomFromFavourites(visitorId, roomId);
        } catch (JTHDataBaseException e){
            return JsonMapRepresentation.result(false, "Delete favourite room: DataBase error (or invalid ids given)", null);
        }

        return JsonMapRepresentation.result(true, "success", null);
    }

}
