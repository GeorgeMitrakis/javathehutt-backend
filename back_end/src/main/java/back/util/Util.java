package back.util;

import back.Exceptions.JTHInputException;
import org.restlet.Request;
import org.restlet.data.Form;

public class Util {
    public static void validateArgs(String ... args) throws JTHInputException {
        for(String s: args){
            if(s == null){
                throw new JTHInputException();
            }
        }
    }

    public static String getJWTfromRequest(Request request) throws JTHInputException {
        try{
            return request.getHeaders().getFirstValue("token");
        }catch (Exception e){
            throw new JTHInputException("Could not decode jwt");
        }
    }
}
