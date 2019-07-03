package back.api;
import back.data.RoomsDAO;
import back.exceptions.JTHInputException;
import back.conf.Configuration;
import back.data.ImageDAO;
import back.model.Image;
import back.model.Room;
import back.model.User;
import back.util.ImgFetch;
import back.util.JWT;
import back.util.JsonMapRepresentation;
import org.restlet.data.Form;
import org.restlet.representation.ByteArrayRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;


public class ImageResource extends ServerResource {

    private ImageDAO imageDAO = Configuration.getInstance().getImageDAO();
    private RoomsDAO roomsDAO = Configuration.getInstance().getRoomsDAO();

    @Override
    protected Representation get() throws ResourceException {
        try {
//            String imgIdStr = getQueryValue("imgId");
//            if(imgIdStr == null){
//                throw new JTHInputException("no image id specified");
//            }
//            long imgId = Long.parseLong(imgIdStr);
//            Image img = imageDAO.getById(imgId);
            String randomUrl = imageDAO.getRandomImageUrl();
            if (randomUrl.isEmpty()){
                return JsonMapRepresentation.result(false,"Image not found", null);
            }
            return ImgFetch.fetch(randomUrl);
        } catch (Exception e){
            e.printStackTrace();
            return JsonMapRepresentation.result(false,"Image error", null);
        }
    }

    @Override
    protected Representation post(Representation entity) throws ResourceException {
        try {
            Form form = new Form(entity);
            String url = form.getFirstValue("url");
            if (url == null) throw new JTHInputException("no url specified");
            String roomIdStr =  form.getFirstValue("roomId");
            if (roomIdStr == null) throw new JTHInputException("no room id specified");

            int roomId = Integer.parseInt(roomIdStr);

            // only a owner-provider and an admin should be allowed to post an image for a room
            if (Configuration.CHECK_AUTHORISATION) {
                Room room = roomsDAO.getRoomById(roomId);
                if (room == null){
                    return JsonMapRepresentation.result(false,"Post image: there is no room with that image",null);
                }
                long providerId = room.getProviderId();
                try {
                    String jwt = JWT.getJWTFromHeaders(getRequest());
                    User user = JWT.getUserJWT(jwt);
                    if (!JWT.assertRole(jwt, "admin") && (user == null || providerId != user.getId())) {
                        return JsonMapRepresentation.result(false, "Post image: forbidden (not allowed to post an image for a room of another provider unless admin)", null);
                    }
                } catch (JTHInputException e){
                    return JsonMapRepresentation.result(false,"Post image: " + e.getErrorMsg(),null);
                }
            }

            imageDAO.addImage(roomId, url);
        } catch (JTHInputException e){
            return JsonMapRepresentation.result(false,"Image error: " + e.getErrorMsg(), null);
        } catch (Exception e){
            e.printStackTrace();
            return JsonMapRepresentation.result(false,"Image error", null);
        }
        return JsonMapRepresentation.result(true,"success", null);
    }

    @Override
    protected Representation delete() throws ResourceException {
        try {
            String imgIdStr = getQueryValue("imgId");
            if(imgIdStr == null){
                throw new JTHInputException("no image id specified");
            }
            long imgId = Long.parseLong(imgIdStr);

            // only a owner-provider and an admin should be allowed to delete a room's image
            if (Configuration.CHECK_AUTHORISATION) {
                int roomId = imageDAO.getRoomIdForImage(imgId);
                Room room = roomsDAO.getRoomById(roomId);
                if (room == null){
                    return JsonMapRepresentation.result(false,"Delete image: there is no room with that image",null);
                }
                long providerId = room.getProviderId();
                try {
                    String jwt = JWT.getJWTFromHeaders(getRequest());
                    User user = JWT.getUserJWT(jwt);
                    if (!JWT.assertRole(jwt, "admin") && (user == null || providerId != user.getId())) {
                        return JsonMapRepresentation.result(false, "Delete image: forbidden (not allowed to delete a room's image for another provider unless admin)", null);
                    }
                } catch (JTHInputException e){
                    return JsonMapRepresentation.result(false,"Delete image: " + e.getErrorMsg(),null);
                }
            }

            imageDAO.deleteImage(imgId);
        } catch (NumberFormatException e) {
            return JsonMapRepresentation.result(false,"Image error: non number given to arithmetic parameter", null);
        } catch (JTHInputException e){
            return JsonMapRepresentation.result(false,"Image error: " + e.getErrorMsg(), null);
        } catch (Exception e){
            e.printStackTrace();
            return JsonMapRepresentation.result(false,"Image error", null);
        }
        return JsonMapRepresentation.result(true,"success", null);
    }

}
