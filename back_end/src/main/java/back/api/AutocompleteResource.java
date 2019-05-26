package back.api;

import back.conf.Configuration;
import back.data.RoomsDAO;
import back.data.UserDAO;
import back.util.JsonMapRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;


public class AutocompleteResource extends ServerResource {

    private final UserDAO userDAO = Configuration.getInstance().getUserDAO();
    private final RoomsDAO roomsDAO = Configuration.getInstance().getRoomsDAO();


    // auto completes hardcoded city names
    @Override
    protected Representation get() throws ResourceException {
        //TODO: hardcoded autocomplete on memory with a Trie or hit the database every time?
        return JsonMapRepresentation.forSimpleResult("Hello world!");
    }

}
