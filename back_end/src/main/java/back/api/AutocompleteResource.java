package back.api;

import back.Exceptions.JTHDataBaseException;
import back.conf.Configuration;
import back.data.RoomsDAO;
import back.util.JsonMapRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class AutocompleteResource extends ServerResource {

    private final RoomsDAO roomsDAO = Configuration.getInstance().getRoomsDAO();

    // auto completes hardcoded city names
    @Override
    protected Representation get() throws ResourceException {

        String prefix = getQueryValue("str");
        if (prefix == null || prefix.equals("")){
            return JsonMapRepresentation.result(false, "Autocomplete: Missing or empty \"str\" parameter", null);
        }

        List<String> results;
        try {
            results = roomsDAO.autocompletePrefix(prefix);
        } catch (JTHDataBaseException e){
            return JsonMapRepresentation.result(false, "Autocomplete: DataBase error", null);
        }

        //DEBUG
        for (String name : results){
            System.out.println(name);
        }

        Map<String, Object> m = new HashMap<>();
        m.put("cityNames", results);
        return JsonMapRepresentation.result(true, null, m);
    }

}
