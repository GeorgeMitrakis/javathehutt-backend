package back.api;
import back.Exceptions.JTHInputException;
import back.util.ImgFetch;
import back.util.JsonMapRepresentation;
import org.restlet.representation.ByteArrayRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;


public class ImageResource extends ServerResource {
    @Override
    protected Representation get() throws ResourceException {
        try{
            String imgId = getQueryValue("imgId");
            if(imgId == null){
                throw new JTHInputException("no image id specified");
            }
            ByteArrayRepresentation bar = ImgFetch.fetch("https://homepages.cae.wisc.edu/~ece533/images/cat.png");
            return bar;
        }catch(JTHInputException e){
            return JsonMapRepresentation.result(false,"something went wrong: " + e.getErrorMsg(), null);
        }catch (Exception e){
            e.printStackTrace();
            return JsonMapRepresentation.result(false,"something went wrong", null);
        }



    }
}
