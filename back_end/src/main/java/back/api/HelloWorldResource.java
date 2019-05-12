package back.api;

import back.model.User;
import back.util.JWT;
import com.google.gson.JsonObject;
import org.restlet.data.Form;
import org.restlet.data.Header;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;
import org.restlet.resource.ServerResource;
import back.conf.Configuration;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.restlet.util.Series;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class HelloWorldResource extends org.restlet.resource.ServerResource {

    @Override
    protected Representation get() throws ResourceException {
        return JsonMapRepresentation.forSimpleResult("Hello world!");
    }




}
