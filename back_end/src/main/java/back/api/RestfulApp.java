package back.api;

import org.restlet.Application;
import org.restlet.Restlet;
import org.restlet.routing.Router;

/**
 * The Restlet App, mapping URL patterns to ServerSideResources.
 */
public class RestfulApp extends Application {

	@Override
	public synchronized Restlet createInboundRoot() {

		Router router = new Router(getContext());

		//GET
		router.attach("/hello", HelloWorldResource.class);

		//GET, POST (admin)
		router.attach("/admin/users", UsersResource.class);

		return router;
	}

}