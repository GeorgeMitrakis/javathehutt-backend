package back.api;

import back.exceptions.JTHAuthException;
import back.exceptions.JTHDataBaseException;
import back.conf.Configuration;
import back.data.UserDAO;
import back.model.User;
import back.util.Hashing;
import back.util.JWT;

import back.util.JsonMapRepresentation;
import org.restlet.data.Form;
import org.restlet.representation.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import java.util.HashMap;
import java.util.Map;


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
            return JsonMapRepresentation.result(false,"Login error: missing or empty parameters",null);
        }

        // hash password
        password = Hashing.getHashSHA256(password);

        // check authentication and take the claims with Claims claims = JWT.decodeJWT(jwt);
        try {
            User u = userDAO.getByCredentials(email, password);   // throws JWTAuthException if password is wrong, null if email is wrong
            if (u == null){
                return JsonMapRepresentation.result(false,"Login error: wrong email",null);
            }
            boolean isBanned = userDAO.getUserBan(u.getId());
            if (isBanned){
                return JsonMapRepresentation.result(false,"Login error: user is banned",null);
            }
            String jwt = JWT.createJWT(u, Configuration.getInstance().getLoginTTL());
            Map<String,Object> data = new HashMap<>();
            data.put("token", jwt);
            data.put("user", u);
            return JsonMapRepresentation.result(true,null, data);
        } catch (JTHAuthException e) {
            return JsonMapRepresentation.result(false,"Login error: wrong password",null);
        } catch (JTHDataBaseException e){
            return JsonMapRepresentation.result(false,"Login error: database error",null);
        }
    }


}
