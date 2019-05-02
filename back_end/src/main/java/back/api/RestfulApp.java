package back.api;

import org.restlet.Application;
import org.restlet.Restlet;
import org.restlet.resource.Finder;
import org.restlet.resource.ServerResource;
import org.restlet.routing.Router;

/**
 * The Restlet App, mapping URL patterns to ServerSideResources.
 */
public class RestfulApp extends Application {

	@Override
	public synchronized Restlet createInboundRoot() {

		Router router = new Router(getContext());

		router.attach("/hello", HelloWorldResource.class);

		// POST
		router.attach("/admin", AdminResource.class);

		// POST
		router.attach("/login", LoginResource.class);

		// GET  /users             -> get (my) user info (profile)
		// POST /users?signup=yes  -> sign up
        // PUT  /users?edit=yes    -> edit
		router.attach("/users" , UsersResource.class);

		return router;
	}

}