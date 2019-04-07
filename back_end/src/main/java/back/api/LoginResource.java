package back.api;

import back.conf.Configuration;
import back.data.JTHAuthException;
import back.data.UserDAO;
import back.model.User;
import back.util.TokenFactory;
import org.restlet.data.Form;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


public class LoginResource extends ServerResource {

    private final UserDAO userDAO = Configuration.getInstance().getUserDAO();

    @Override
    protected Representation post(Representation entity) throws ResourceException {

        try{
            Optional<User> u = userDAO.getByCredentials("test@test.com","asdf");
            if(!u.isPresent()) {
                throw new JTHAuthException();
            }
            Map<String,Object> claimsMap = new HashMap<String, Object>();
            claimsMap.put("userid", Long.toString(u.get().getId()));
            String jws = TokenFactory.getTokenFor(claimsMap);
            return JsonMapRepresentation.forSimpleResult(jws);

        }catch (Exception e){
            e.printStackTrace();
            return JsonMapRepresentation.forSimpleResult("No login for you!");
        }






    }
    
}
