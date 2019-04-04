package back_end.api;

import org.back_end.representation.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;
import ys09.conf.Configuration;

import java.util.HashMap;
import java.util.Map;

public class HelloWorldResource extends ServerResource {

    @Override
    protected Representation get() throws ResourceException {
        return JsonMapRepresentation.forSimpleResult("Hello world!");
    }
}
