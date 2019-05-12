package back.api;

import back.conf.Configuration;
import back.data.BookingDAO;
import back.data.RoomsDAO;
import back.data.UserDAO;
import back.model.Room;
import back.model.User;
import back.util.DateHandler;
import back.util.JWT;
import org.restlet.data.Form;
import org.restlet.representation.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;


public class BookingResource  extends ServerResource {

    private final BookingDAO bookingDAO = Configuration.getInstance().getBookingDAO();
    private final UserDAO userDAO = Configuration.getInstance().getUserDAO();
    private final RoomsDAO roomsDAO = Configuration.getInstance().getRoomsDAO();


    @Override
    protected Representation post(Representation entity) throws ResourceException {
        //Create a new restlet form
        Form form = new Form(entity);

        //Read the parameters
        String userIdStr = form.getFirstValue("userId");
        String roomIdStr = form.getFirstValue("roomId");
        String startDate = form.getFirstValue("startDate");
        String endDate = form.getFirstValue("endDate");

        if (userIdStr == null || roomIdStr == null || startDate == null || endDate == null ||
            userIdStr.equals("") || roomIdStr.equals("") || startDate.equals("") || endDate.equals("")) {
            return JsonMapRepresentation.result(false,"Booking error: missing or empty parameters",null);
        }

        long userId;
        int roomId;
        try {
            userId = Long.parseLong(userIdStr);
            roomId = Integer.parseInt(roomIdStr);
        } catch (ArithmeticException e){
            return JsonMapRepresentation.result(false,"Booking error: id parameters that are not numbers",null);
        }

        // TODO: also check authentication from jasonWebToken, userId must be the id of the user making the request!

        // check that user exists
        User user = userDAO.getById(userId);
        if (user == null){
            return JsonMapRepresentation.result(false,"Booking error: user does not exist",null);
        }

        // check that room exists
        Room room = roomsDAO.getRoomById(roomId);
        if (room == null){
            return JsonMapRepresentation.result(false,"Booking error: room does not exist",null);
        }

        // book the room
        boolean success = bookingDAO.bookRoomForVisitor(user, room, DateHandler.FrontDateToSQLDate(startDate), DateHandler.FrontDateToSQLDate(endDate));

        if (!success){
            return JsonMapRepresentation.result(false,"Booking error: database error",null);
        } else {
            return JsonMapRepresentation.result(true,"Booking of room successful",null);
        }
    }

}
