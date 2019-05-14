package back.api;

import back.Exceptions.JTHAuthException;
import back.model.User;


public class JTHAuth {

    public static void authorize(User requester, AdminResource r ) throws JTHAuthException {
        if(requester.getRole() != "admin"){
            throw new JTHAuthException();
        }
    }
}
