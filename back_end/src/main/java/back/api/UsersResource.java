package back.api;

import back.data.JTHAuthException;
import back.model.Provider;
import back.model.Visitor;
import back.util.Hashing;
import back.util.JWT;
import back.util.Util;
import org.restlet.data.Form;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;
import back.conf.Configuration;
import back.data.UserDAO;
import back.data.jdbc.DataAccess;
import back.data.Limits;
import back.model.User;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class UsersResource extends ServerResource {

    private final UserDAO userDAO = Configuration.getInstance().getUserDAO();

    @Override
    protected Representation get() throws ResourceException {
        // TODO: return user profile info
        String email = getQueryValue("email");
        if(email != null){
            User u = userDAO.getByEmail(email);
            if(u == null){
                return JsonMapRepresentation.result(false, null,null);
            }else{
                Map<String, Object> m = new HashMap<>();
                m.put("user",u);
                return JsonMapRepresentation.result(true,null,m);
            }
        }
        return JsonMapRepresentation.result(false,"no parameters given",null);
    }



    @Override
    protected Representation post(Representation entity) throws ResourceException {
        User createdUser = null;
        // Registers a new user
        try{
            Form form = new Form(entity);
            String email = form.getFirstValue("email");
            String password = form.getFirstValue("password");
            String password1 = form.getFirstValue("password1");
            String type = form.getFirstValue("type");

            if ( email == null || password == null || password1 == null || type == null
                 || password.equals("") || password1.equals("") || type.equals("")){
                throw new JTHInputException("missing or empty parameters");
            }
            else if ( !password.equals(password1) ){
                throw new JTHInputException("mismatching password");
            }
            else if (userDAO.getByEmail(email) != null){
                throw new JTHInputException("email is already taken");
            }

            // hash password
            password = Hashing.getHashSHA256(password);

            boolean success = false;
            switch (type){
                case "visitor":
                    String name =  form.getFirstValue("name");
                    String surname =  form.getFirstValue("surname");
                    if (name == null || surname == null || name.equals("") || surname.equals("")) throw new JTHInputException("missing or empty parameters");
                    createdUser = new Visitor(0, email, name, surname);
                    success = userDAO.storeUser((Visitor) createdUser, password);
                    break;
                case "provider":
                    String providername =  form.getFirstValue("providername");
                    if (providername == null || providername.equals("")) throw new JTHInputException("missing or empty parameters");
                    createdUser = new Provider(0, email, providername);
                    success = userDAO.storeUser((Provider) createdUser, password);
                    break;
                default:
                    throw new JTHInputException("unknown type");
            }
            if (!success){
                throw new JTHInputException("database error");
            }
        } catch (JTHInputException e){
            return JsonMapRepresentation.result(false,"Sign up error: " + e.getErrorMsg(),null);
        }
        //auto login

        if(getQueryValue("autologin") != null && createdUser != null){
            String jwt = JWT.createJWT(createdUser,1000000);
            Map<String, Object> data = new HashMap<>();
            data.put("user", createdUser);
            data.put("token", jwt);
            return JsonMapRepresentation.result(true,"also logged in", data);
        }
        return JsonMapRepresentation.result(true,null,null);


    }

    @Override
    protected Representation put(Representation entity) throws ResourceException {
        // TODO: updates user information
        return null;
    }

}
