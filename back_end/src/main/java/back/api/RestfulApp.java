package back.api;

import back.model.Room;
import org.restlet.Application;
import org.restlet.Restlet;
import org.restlet.data.Reference;
import org.restlet.resource.Directory;
import org.restlet.resource.Finder;
import org.restlet.resource.ServerResource;
import org.restlet.routing.Router;
import org.restlet.routing.Template;
import org.restlet.service.CorsService;

import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * The Restlet App, mapping URL patterns to ServerSideResources.
 */


public class RestfulApp extends Application {

	public RestfulApp() {

		CorsService corsService = new CorsService();
		corsService.setAllowingAllRequestedHeaders(true);
		corsService.setAllowedOrigins(new HashSet(Arrays.asList("*")));
		corsService.setAllowedCredentials(true);
		getServices().add(corsService);
	}

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

		router.attach("/dummy", DummyResource.class);

		// TODO: For autocomplete GET /room_autocomplete?str=<input>
		router.attach("/room_autocomplete", AutocompleteResource.class);

		// TODO: search room
		router.attach("/search", RoomSearhResource.class);

		// TODO: Book
		// POST /book (userId, roomId, startDate, endDate)
		router.attach("/book", BookingResource.class);

		return router;
	}




}