package back.api;

import back.exceptions.JTHInputException;
import back.conf.Configuration;
import back.data.ImageDAO;
import back.util.JsonMapRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RoomImagesResource extends ServerResource {
    private ImageDAO imageDAO = Configuration.getInstance().getImageDAO();

    @Override
    protected Representation get() throws ResourceException {
        try {
            String roomIdStr = getQueryValue("roomId");
            if (roomIdStr == null){
                throw new JTHInputException("missing roomId parameter");
            }

            int roomId;
            try {
                roomId = Integer.parseInt(roomIdStr);
            } catch (ArithmeticException e) {
                throw new JTHInputException("roomId given is not a number");
            }
            List<Long> ids = imageDAO.getRoomImageIds(roomId);
            Map<String, Object> res = new HashMap<>();
            res.put("ids", ids);
            return JsonMapRepresentation.result(true,"here's your ids!", res );
        } catch(JTHInputException e){
            return JsonMapRepresentation.result(false,"something went wrong: " + e.getErrorMsg(), null);
        } catch (Exception e){
            e.printStackTrace();
            return JsonMapRepresentation.result(false,"something went wrong", null);
        }
    }
}
