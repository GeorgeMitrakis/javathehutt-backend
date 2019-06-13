package back.api;

import back.util.JsonMapRepresentation;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.restlet.representation.Representation;

import org.restlet.representation.StringRepresentation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;


public class DummyResource extends ServerResource {


    @Override
    protected Representation get() throws ResourceException {


        JSONParser jsonParser = new JSONParser();

        String field = getQueryValue("field");
        try (FileReader reader = new FileReader("./dummy.json"))
        {
            //Read JSON file
            Object obj = jsonParser.parse(reader);
            return new StringRepresentation(((org.json.simple.JSONObject)obj).get(field).toString());

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }


        return JsonMapRepresentation.forSimpleResult("Hello world!");
    }


}
