package back.api;

import back.data.JTHAuthException;
import back.model.Admin;
import back.model.User;
import org.restlet.resource.ServerResource;

public class JTHAuth {

    public static void authorize(User requester, AdminResource r ) throws JTHAuthException {
        if(requester.getRole() != "admin"){
            throw new JTHAuthException();
        }
    }
}
