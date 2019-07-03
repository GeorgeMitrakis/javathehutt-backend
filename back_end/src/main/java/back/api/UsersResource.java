package back.api;

import back.exceptions.JTHAuthException;
import back.exceptions.JTHDataBaseException;
import back.exceptions.JTHInputException;
import back.data.Limits;
import back.model.Provider;
import back.model.Visitor;
import back.util.Hashing;
import back.util.JWT;
import back.util.JsonMapRepresentation;
import org.restlet.data.Form;
import org.restlet.representation.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;
import back.conf.Configuration;
import back.data.UserDAO;
import back.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UsersResource extends ServerResource {

    private final UserDAO userDAO = Configuration.getInstance().getUserDAO();

    // GET /users?id=...    -> returns user with that id
    // GET /users?email=... -> returns user with that email
    // GET /users?emailPrefix=... -> returns all users with given prefix on their email
    // GET /users           -> returns all users
    // GET /users?role=...  -> returns all users of that role
    @Override
    protected Representation get() throws ResourceException {
        try {
            String idStr = getQueryValue("id");
            String email = getQueryValue("email");
            String emailPrefix = getQueryValue("emailPrefix");
            String role = getQueryValue("role");
            if (idStr != null) {
                User u = userDAO.getById(Long.parseLong(idStr));
                if (u == null) {
                    return JsonMapRepresentation.result(false, "user does not exist", null);
                } else {
                    Map<String, Object> m = new HashMap<>();
                    m.put("user", u);
                    return JsonMapRepresentation.result(true, null, m);
                }
            } else if (email != null) {
                User u = userDAO.getByEmail(email);
                if (u == null) {
                    return JsonMapRepresentation.result(false, null, null);
                } else {
                    Map<String, Object> m = new HashMap<>();
                    m.put("user", u);
                    return JsonMapRepresentation.result(true, null, m);
                }
            } else if (emailPrefix != null) {
                List<User> matchingUsers = userDAO.getUsersByEmailPrefix(emailPrefix);

                if (Configuration.CHECK_AUTHORISATION) {
                    try {
                        String jwt = JWT.getJWTFromHeaders(getRequest());
                        if (!JWT.assertRole(jwt, "admin")){
                            return JsonMapRepresentation.result(false,"forbidden (only an admin can request all users by email prefix)",null);
                        }
                    } catch (JTHInputException e){
                        return JsonMapRepresentation.result(false, e.getErrorMsg(),null);
                    }
                }

                if (matchingUsers == null) {
                    return JsonMapRepresentation.result(false, "database error (null)", null);
                } else {
                    Map<String, Object> m = new HashMap<>();
                    m.put("users", matchingUsers);
                    return JsonMapRepresentation.result(true, null, m);
                }
            } else {  // return all users
                List<User> allUsers = userDAO.getUsers(new Limits(0, (int) userDAO.countUsers()));
                Map<String, Object> m = new HashMap<>();

                if (Configuration.CHECK_AUTHORISATION) {
                    try {
                        String jwt = JWT.getJWTFromHeaders(getRequest());
                        if (!JWT.assertRole(jwt, "admin")){
                            return JsonMapRepresentation.result(false,"forbidden (only an admin can request all users)",null);
                        }
                    } catch (JTHInputException e){
                        return JsonMapRepresentation.result(false, e.getErrorMsg(),null);
                    }
                }

                // remove unwanted
                if (role != null && (role.equals("visitor") || role.equals("provider") || role.equals("admin"))){
                    for (User u : allUsers){
                        if (u.getRole().equals(role)) allUsers.remove(u);
                    }
                }

                m.put("users", allUsers);
                return JsonMapRepresentation.result(true, null, m);
            }
        } catch (JTHDataBaseException e){
            return JsonMapRepresentation.result(false,"database error", null);
        } catch (NumberFormatException e){
            return JsonMapRepresentation.result(false,"id given is not a number", null);
        }
    }


    @Override
    protected Representation post(Representation entity) throws ResourceException {
        User createdUser;
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

            switch (type){
                case "visitor":
                    String name =  form.getFirstValue("name");
                    String surname =  form.getFirstValue("surname");
                    if (name == null || surname == null || name.equals("") || surname.equals("")) throw new JTHInputException("missing or empty parameters");
                    createdUser = new Visitor(0, email, name, surname);
                    userDAO.storeUser((Visitor) createdUser, password);
                    break;
                case "provider":
                    String providername =  form.getFirstValue("providername");
                    if (providername == null || providername.equals("")) throw new JTHInputException("missing or empty parameters");
                    createdUser = new Provider(0, email, providername);
                    userDAO.storeUser((Provider) createdUser, password);
                    break;
                default:
                    throw new JTHInputException("unknown type");
            }
        } catch (JTHDataBaseException e){
            return JsonMapRepresentation.result(false,"Sign up error: database error",null);
        } catch (JTHInputException e){
            return JsonMapRepresentation.result(false,"Sign up error: " + e.getErrorMsg(),null);
        }

        // auto login
        if(getQueryValue("autologin") != null && createdUser != null){
            String jwt = JWT.createJWT(createdUser,1000000);
            Map<String, Object> data = new HashMap<>();
            data.put("user", createdUser);
            data.put("token", jwt);
            return JsonMapRepresentation.result(true,"also logged in", data);
        }
        return JsonMapRepresentation.result(true,"registration successful",null);
    }

    @Override
    protected Representation put(Representation entity) throws ResourceException {
        Form form = new Form(entity);

        String userIdStr = form.getFirstValue("userId");
        String newemail = form.getFirstValue("email");
        String newpassword = form.getFirstValue("newPassword");
        String oldpassword = form.getFirstValue("oldPassword");
        String name = form.getFirstValue("name");
        String surname = form.getFirstValue("surname");
        String providerName = form.getFirstValue("providerName");

        long userId;
        try{
            userId = Long.parseLong(userIdStr);
        } catch (NumberFormatException e){
            return JsonMapRepresentation.result(false,"Update error: non number user id given",null);
        }

        if (newpassword != null && oldpassword == null){
            return JsonMapRepresentation.result(false,"Update error: need to give old password in order to update current one",null);
        }

        try {
            if (userDAO.getByEmail(newemail) != null){
                return JsonMapRepresentation.result(false,"Update error: email given is already taken",null);
            }
        } catch (JTHDataBaseException e) {
            return JsonMapRepresentation.result(false,"Update error: database error",null);
        }

        if (Configuration.CHECK_AUTHORISATION) {
            try {
                String jwt = JWT.getJWTFromHeaders(getRequest());
                if (!JWT.assertRole(jwt, "admin") || !JWT.assertUser(jwt, userId)){
                    return JsonMapRepresentation.result(false,"Update error: forbidden, only admin or user himself can change his information",null);
                }
            } catch (JTHInputException e){
                return JsonMapRepresentation.result(false,"Update error: " + e.getErrorMsg(),null);
            }
        }

        // hash passwords
        if (oldpassword != null) oldpassword = Hashing.getHashSHA256(oldpassword);
        if (newpassword != null) newpassword = Hashing.getHashSHA256(newpassword);

        try {
            boolean exists = userDAO.updateUserInfo(userId, newemail, newpassword, oldpassword, name, surname, providerName);
            if (!exists) return JsonMapRepresentation.result(false,"Update error: there is no user with the given id",null);
        } catch (JTHAuthException e) {
            return JsonMapRepresentation.result(false,"Update error: invalid password given",null);
        } catch (JTHDataBaseException e) {
            return JsonMapRepresentation.result(false,"Update error: database error",null);
        }
        return JsonMapRepresentation.result(true,"success",null);
    }

}
