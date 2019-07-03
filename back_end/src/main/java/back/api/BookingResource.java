package back.api;

import back.exceptions.JTHDataBaseException;
import back.exceptions.JTHInputException;
import back.conf.Configuration;
import back.data.BookingDAO;
import back.data.RoomsDAO;
import back.data.UserDAO;
import back.model.Room;
import back.model.Transaction;
import back.model.User;
import back.util.DateHandler;
import back.util.JWT;
import back.util.JsonMapRepresentation;
import org.restlet.data.Form;
import org.restlet.representation.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


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
        String occupantsStr = form.getFirstValue("occupants");

        if (userIdStr == null || roomIdStr == null || startDate == null || endDate == null ||
            userIdStr.equals("") || roomIdStr.equals("") || startDate.equals("") || endDate.equals("")) {
            return JsonMapRepresentation.result(false,"Booking error: missing or empty parameters",null);
        }

        long userId;
        int roomId, occupants;
        try {
            userId = Long.parseLong(userIdStr);
            roomId = Integer.parseInt(roomIdStr);
            occupants = Integer.parseInt(occupantsStr);
        } catch (NumberFormatException e){
            return JsonMapRepresentation.result(false,"Booking error: id parameters that are not numbers",null);
        }

        if (Configuration.CHECK_AUTHORISATION) {
            try {
                String jwt = JWT.getJWTFromHeaders(getRequest());
                if (!JWT.assertRole(jwt, "visitor")){
                    return JsonMapRepresentation.result(false,"Booking error: forbidden (not a visitor)",null);
                } else if (!JWT.assertUser(jwt, userId)){
                    return JsonMapRepresentation.result(false,"Booking error: forbidden (not allowed to book room for other user)",null);
                }
            } catch (JTHInputException e){
                return JsonMapRepresentation.result(false,"Booking error: " + e.getErrorMsg(),null);
            }
        }

        try {
            // check that user exists
            User user = userDAO.getById(userId);
            if (user == null) {
                return JsonMapRepresentation.result(false, "Booking error: user does not exist", null);
            }

            // check that room exists
            Room room = roomsDAO.getRoomById(roomId);
            if (room == null) {
                return JsonMapRepresentation.result(false, "Booking error: room does not exist", null);
            }

            // check that occupants < max occupants for room
            if (occupants > room.getMaxOccupants()){
                return JsonMapRepresentation.result(false, "Booking error: too many occupants (" + occupantsStr + ") for this room (max is " + room.getMaxOccupants() + ")", null);
            }

            // book the room
            boolean succ = bookingDAO.bookRoomForVisitor(user, room, DateHandler.FrontDateToSQLDate(startDate), DateHandler.FrontDateToSQLDate(endDate), occupants);
            if (!succ) {
                return JsonMapRepresentation.result(false, "Booking error: No room available", null);
            } else {
                return JsonMapRepresentation.result(true, "Booking of room successful", null);
            }

        } catch (JTHDataBaseException e) {
            return JsonMapRepresentation.result(false, "Booking error: database error", null);
        }
    }

    public Representation get() throws ResourceException{
        String providerIdStr = getQueryValue("providerId");
        String visitorIdStr = getQueryValue("visitorId");
        if (providerIdStr == null && visitorIdStr == null) {
            if (Configuration.CHECK_AUTHORISATION) {
                try {
                    String jwt = JWT.getJWTFromHeaders(getRequest());
                    if (!JWT.assertRole(jwt, "admin")) {
                        return JsonMapRepresentation.result(false, "Booking error: forbidden (not an admin)", null);
                    }
                } catch (JTHInputException e) {
                    return JsonMapRepresentation.result(false, "Booking error: " + e.getErrorMsg(), null);
                }
            }
            try {
                List<Transaction> res = bookingDAO.getTransactions();
                Map<String, Object> m = new HashMap<>();
                m.put("transactions", res);
                return JsonMapRepresentation.result(true, "success", m);
            } catch (Exception e) {
                e.printStackTrace();
                return JsonMapRepresentation.result(false, "Something went wrong:" + e.getMessage(), null);
            }
        } else if (providerIdStr != null) {
            long providerId;
            try {
                providerId = Long.parseLong(providerIdStr);
            } catch (NumberFormatException e){
                return JsonMapRepresentation.result(false, "Transaction error: arithmetic parameter(s) given not a number", null);
            }

            String profit = getQueryValue("profit");
            if (profit != null) {
                double systemProfit;
                try {
                    systemProfit = bookingDAO.calcProviderProfit(providerId);
                } catch (JTHDataBaseException e) {
                    return JsonMapRepresentation.result(false, "Transaction error: database error", null);
                }

                Map<String, Object> m = new HashMap<>();
                m.put("profit", systemProfit);
                return JsonMapRepresentation.result(true, "success", m);
            } else {
                List<Transaction> transactions;
                try {
                    transactions = bookingDAO.getTransactionsForProvider(providerId);
                } catch (JTHDataBaseException e) {
                    return JsonMapRepresentation.result(false, "Transaction error: database error", null);
                }

                Map<String, Object> m = new HashMap<>();
                m.put("transactions", transactions);
                return JsonMapRepresentation.result(true, "success", m);
            }
        } else {   // visitorIdStr != null
            long visitorId;
            try {
                visitorId = Long.parseLong(visitorIdStr);
            } catch (NumberFormatException e){
                return JsonMapRepresentation.result(false, "Transaction error: arithmetic parameter(s) given not a number", null);
            }

            List<Transaction> transactions;
            try {
                transactions = bookingDAO.getTransactionsForVisitor(visitorId);
            } catch (JTHDataBaseException e) {
                return JsonMapRepresentation.result(false, "Transaction error: database error", null);
            }

            Map<String, Object> m = new HashMap<>();
            m.put("transactions", transactions);
            return JsonMapRepresentation.result(true, "success", m);
        }
    }

}
