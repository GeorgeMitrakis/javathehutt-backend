package back.api;

import back.Exceptions.JTHDataBaseException;
import back.conf.Configuration;
import back.data.RoomsDAO;
import back.model.Rating;
import back.util.JsonMapRepresentation;
import org.restlet.data.Form;
import org.restlet.representation.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RatingResource extends ServerResource {

    private RoomsDAO roomsDAO = Configuration.getInstance().getRoomsDAO();

    @Override
    protected Representation get() throws ResourceException {

        String roomIdStr = getQueryValue("roomId");
        if (roomIdStr == null || roomIdStr.equals("")){
            return JsonMapRepresentation.result(false, "Get ratings: Missing or empty parameter (room id)", null);
        }

        int roomId;
        try {
            roomId = Integer.parseInt(roomIdStr);
        } catch (ArithmeticException e){
            return JsonMapRepresentation.result(false, "Get ratings: room id given is not a number", null);
        }

        List<Rating> ratings;
        try {
            ratings = roomsDAO.getRatingsForRoom(roomId);
        } catch (JTHDataBaseException e){
            return JsonMapRepresentation.result(false, "Get ratings: DataBase error (or invalid room id given)", null);
        }

        Map<String, Object> m = new HashMap<>();
        m.put("ratings", ratings);
        return JsonMapRepresentation.result(true, "success", m);
    }

    @Override
    protected Representation post(Representation entity) throws ResourceException {

        Form form = new Form(entity);

        String visitorIdStr = form.getFirstValue("visitorId");
        String roomIdStr = form.getFirstValue("roomId");
        String starsStr = form.getFirstValue("stars");
        String comment = form.getFirstValue("comment");
        if (roomIdStr == null || roomIdStr.equals("") || visitorIdStr == null || visitorIdStr.equals("") ||
            starsStr == null || starsStr.equals("") || comment == null || comment.equals("")
        ){
            return JsonMapRepresentation.result(false, "Post rating: Missing or empty parameter(s)", null);
        }

        long visitorId;
        int roomId, stars;
        try {
            visitorId = Long.parseLong(visitorIdStr);
            roomId = Integer.parseInt(roomIdStr);
            stars = Integer.parseInt(starsStr);
        } catch (ArithmeticException e){
            return JsonMapRepresentation.result(false, "Post rating: parameter(s) given is not a number", null);
        }

        try {
            boolean res = roomsDAO.addRatingToRoom(visitorId, roomId, stars, comment);
            if (!res){
                return JsonMapRepresentation.result(false, "Post rating: stars must be between 0 and 5", null);
            }
        } catch (JTHDataBaseException e){
            return JsonMapRepresentation.result(false, "Post rating: DataBase error (or invalid ids given)", null);
        }

        return JsonMapRepresentation.result(true, "success", null);
    }


    @Override
    protected Representation delete() throws ResourceException {

        String ratingIdStr = getQueryValue("ratingId");
        if (ratingIdStr == null || ratingIdStr.equals("")){
            return JsonMapRepresentation.result(false, "Delete rating: Missing or empty parameter", null);
        }

        int ratingId;
        try {
            ratingId = Integer.parseInt(ratingIdStr);
        } catch (ArithmeticException e){
            return JsonMapRepresentation.result(false, "Delete rating: parameter given is not a number", null);
        }

        try {
            roomsDAO.removeRatingFromRoom(ratingId);
        } catch (JTHDataBaseException e){
            return JsonMapRepresentation.result(false, "Delete rating: DataBase error (or invalid id given)", null);
        }

        return JsonMapRepresentation.result(true, "success", null);
    }

}
