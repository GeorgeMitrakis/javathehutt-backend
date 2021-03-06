package back.util;

import com.google.gson.Gson;
import org.restlet.data.MediaType;
import org.restlet.representation.WriterRepresentation;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

public class JsonMapRepresentation extends WriterRepresentation {

    private final Map map;

    public JsonMapRepresentation(Map map) {
        super(MediaType.APPLICATION_JSON);
        this.map = map;
    }

    @Override
    public void write(Writer writer) throws IOException {
        Gson gson = new Gson();
        writer.write(gson.toJson(map));
    }

    public static JsonMapRepresentation forSimpleResult(Object o) {
        Map<String, Object> map = new HashMap<>();
        map.put("result", o);
        return new JsonMapRepresentation(map);
    }


    public static JsonMapRepresentation result(boolean success, String message, Map data){
        HashMap<String,Object> res = new HashMap<>();
        res.put("success",success);
        res.put("message",message);
        res.put("data",data);
        return new JsonMapRepresentation(res);
    }
}
