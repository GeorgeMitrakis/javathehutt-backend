package back.api;

import back.exceptions.JTHDataBaseException;
import back.exceptions.JTHInputException;
import back.conf.Configuration;
import back.data.UserDAO;
import back.model.User;
import back.util.JWT;
import back.util.JsonMapRepresentation;
import org.restlet.data.Form;
import org.restlet.representation.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;


public class AdminResource  extends ServerResource {

    private final UserDAO userDAO = Configuration.getInstance().getUserDAO();

    // POST (id, option=ban)     ->  ban user
    // POST (id, option=unban)   ->  unban user
    // POST (id, option=delete)  ->  delete user
    // POST (id, option=promote) ->  promote user to admin
    @Override
    protected Representation post(Representation entity) throws ResourceException {
        String option;
        try {
            Form form = new Form(entity);
            String uidStr = form.getFirstValue("id");
            if (uidStr == null || uidStr.equals("")){
                throw new JTHInputException("user id parameter missing or empty");
            }
            long userId;
            try {
                userId = Long.parseLong(uidStr);
            } catch (NumberFormatException e) {
                throw new JTHInputException("user id given is not a number");
            }
            option = form.getFirstValue("option");
            if (option == null || option.equals("")){
                throw new JTHInputException("option parameter missing or empty");
            }
            User user = userDAO.getById(userId);
            if (user == null){
                throw new JTHInputException("user id does not exist");
            }

            if (Configuration.CHECK_AUTHORISATION) {
                try {
                    String jwt = JWT.getJWTFromHeaders(getRequest());
                    if (!JWT.assertRole(jwt, "admin")){
                        return JsonMapRepresentation.result(false,"Admin action error: forbidden (not an admin)",null);
                    }
                } catch (JTHInputException e){
                    return JsonMapRepresentation.result(false,"Admin action error: " + e.getErrorMsg(),null);
                }
            }

            switch (option){
                case "ban":
                    userDAO.setUserBan(user, true);
                    break;
                case "unban":
                    userDAO.setUserBan(user, false);
                    break;
                case "promote":
                    userDAO.promoteUserToAdmin(user);
                    break;
                case "delete":
                    userDAO.deleteUser(user);
                    break;
                default:
                    throw new JTHInputException("invalid admin option");
            }
        } catch (JTHInputException e){
            return JsonMapRepresentation.result(false, "Admin action error: " + e.getErrorMsg(), null);
        } catch (JTHDataBaseException e){
            return JsonMapRepresentation.result(false, "Admin action error: database error", null);
        }
        return JsonMapRepresentation.result(true, option + " successful", null);
    }

}
