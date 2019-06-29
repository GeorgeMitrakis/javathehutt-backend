package back.api;
import back.exceptions.JTHInputException;
import back.conf.Configuration;
import back.data.ImageDAO;
import back.model.Image;
import back.util.ImgFetch;
import back.util.JsonMapRepresentation;
import org.restlet.representation.ByteArrayRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;


public class ImageResource extends ServerResource {
    private ImageDAO imageDAO = Configuration.getInstance().getImageDAO();

    @Override
    protected Representation get() throws ResourceException {
        try{
            String imgIdStr = getQueryValue("imgId");
            if(imgIdStr == null){
                throw new JTHInputException("no image id specified");
            }
            long imgId = Long.parseLong(imgIdStr);
            Image img = imageDAO.getById(imgId);
            ByteArrayRepresentation bar = ImgFetch.fetch(img.getUrl());
            return bar;
        }catch(JTHInputException e){
            return JsonMapRepresentation.result(false,"something went wrong: " + e.getErrorMsg(), null);
        }catch (Exception e){
            e.printStackTrace();
            return JsonMapRepresentation.result(false,"something went wrong", null);
        }
    }
}
