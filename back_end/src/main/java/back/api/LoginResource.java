package back.api;

import back.conf.Configuration;
import back.data.JTHAuthException;
import back.data.UserDAO;
import back.model.User;
import back.util.JWT;
import back.util.TokenFactory;
import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
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
import io.jsonwebtoken.Claims;

public class LoginResource extends ServerResource {

    private final UserDAO userDAO = Configuration.getInstance().getUserDAO();



    @Override
    protected Representation post(Representation entity) throws ResourceException {
//        check authentication and take the claims with Claims claims = JWT.decodeJWT(jwt);
        try{
            Optional<User> u = userDAO.getByCredentials("test@test.com","asdf");
            if(!u.isPresent()) {
                throw new JTHAuthException();
            }
            String jwt = JWT.createJWT(u.get(),Configuration.getInstance().getLoginTTL());
            //String jwt = JWT.createJWT(u.get().getId(), u.get().getEmail(), "subject", 800000);
            return JsonMapRepresentation.forSimpleResult(jwt);

        }catch (Exception e){
            e.printStackTrace();
            return JsonMapRepresentation.forSimpleResult("No login for you!");
        }
    }


}
