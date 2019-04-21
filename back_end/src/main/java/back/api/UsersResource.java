package back.api;

import back.model.Provider;
import back.model.Visitor;
import back.util.Util;
import org.restlet.data.Form;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;
import back.conf.Configuration;
import back.data.UserDAO;
import back.data.jdbc.DataAccess;
import back.data.Limits;
import back.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UsersResource extends ServerResource {

    private final UserDAO userDAO = Configuration.getInstance().getUserDAO();

    @Override
    protected Representation get() throws ResourceException {

        //we assume that we have the ADMIN role. Your should write code to ensure it.
        //Consult Restlet filters for implementing your code as a filter.

        //read request parameters to create the limits
        //String start = getQueryValue("start");
        //...
        Limits limits = new Limits(0, 10);
        List<User> users = userDAO.getUsers(limits);

        Map<String, Object> map = new HashMap<>();
        map.put("start", limits.getStart());
        map.put("count", limits.getCount());
        map.put("total", limits.getTotal());
        map.put("results", users);

        return new JsonMapRepresentation(map);
    }

    @Override
    protected Representation post(Representation entity) throws ResourceException {

        try{
            Form form = new Form(entity);
            String email = form.getFirstValue("username");
            if(userDAO.getByEmail(email).isPresent()){
                throw new JTHInputException();
            }
            String type = form.getFirstValue("type");
            switch (type){
                case "visitor":
                    return visitorSignUp(form);
                case "provider":
                    return providerSignUp(form);
            }
        }catch (JTHInputException e){
            return JsonMapRepresentation.forSimpleResult("No signup for you");
        }
        return  JsonMapRepresentation.forSimpleResult("Success");

    }

    private Representation providerSignUp(Form form){
        //...
        try{
            String password = form.getFirstValue("password");
            String email = form.getFirstValue("email");
            String name = form.getFirstValue("name");
            String surname = form.getFirstValue("surname");

            Util.validateArgs(password,email,name,surname);

            Provider p = new Provider(0,email,name, surname);
            userDAO.storeUser(p,password);
        }catch(JTHInputException e){
            return JsonMapRepresentation.forSimpleResult(false);
        }
        return JsonMapRepresentation.forSimpleResult(true);
    }

    private Representation visitorSignUp(Form form) throws ResourceException{
        try{
            String password = form.getFirstValue("password");
            String email = form.getFirstValue("email");
            String name = form.getFirstValue("name");
            String surname = form.getFirstValue("surname");

            Util.validateArgs(password,email,name,surname);

            Visitor v = new Visitor(0,email,name,surname);
            userDAO.storeUser(v,password);
        }catch(JTHInputException e){
            return JsonMapRepresentation.forSimpleResult(false);
        }
        return JsonMapRepresentation.forSimpleResult(true);
    }
}
