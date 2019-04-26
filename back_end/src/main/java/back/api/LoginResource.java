package back.api;

import back.conf.Configuration;
import back.data.JTHAuthException;
import back.data.UserDAO;
import back.model.User;
import back.util.Hashing;
import back.util.JWT;

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
        //Create a new restlet form
        Form form = new Form(entity);

        //Read the parameters
        String email = form.getFirstValue("email");
        String password = form.getFirstValue("password");

        if (email == null || password == null || email.equals("") || password.equals("")) {
            return JsonMapRepresentation.forSimpleResult("Login error: missing or empty parameters");
        }

        // hash password
        password = Hashing.getHashSHA256(password);

        // check authentication and take the claims with Claims claims = JWT.decodeJWT(jwt);
        try {
            User u = userDAO.getByCredentials(email, password);   // throws JWTAuthException if return value would be null
            String jwt = JWT.createJWT(u, Configuration.getInstance().getLoginTTL());
            //String jwt = JWT.createJWT(u.get().getId(), u.get().getEmail(), "subject", 800000);
            return JsonMapRepresentation.forSimpleResult(jwt);
        } catch (Exception e) {
            e.printStackTrace();
            return JsonMapRepresentation.forSimpleResult("Login error: incorrect credentials");
        }
    }


}
