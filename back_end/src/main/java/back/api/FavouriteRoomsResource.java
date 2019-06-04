package back.api;

import back.Exceptions.JTHDataBaseException;
import back.conf.Configuration;
import back.data.BookingDAO;
import back.util.JsonMapRepresentation;
import org.restlet.data.Form;
import org.restlet.representation.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

public class FavouriteRoomsResource extends ServerResource {

    private final BookingDAO bookingDAO = Configuration.getInstance().getBookingDAO();


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

        try {
            bookingDAO.removeRoomFromFavourites(visitorId, roomId);
        } catch (JTHDataBaseException e){
            return JsonMapRepresentation.result(false, "Delete favourite room: DataBase error (or invalid ids given)", null);
        }

        return JsonMapRepresentation.result(true, "success", null);
    }

}
