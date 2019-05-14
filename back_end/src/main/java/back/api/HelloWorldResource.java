package back.api;

import org.restlet.representation.Representation;
import org.restlet.resource.ResourceException;


public class HelloWorldResource extends org.restlet.resource.ServerResource {

    @Override
    protected Representation get() throws ResourceException {
        return JsonMapRepresentation.forSimpleResult("Hello world!");
    }


}
