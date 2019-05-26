package back.api;

import org.restlet.Application;
import org.restlet.Restlet;
import org.restlet.routing.Router;
import org.restlet.service.CorsService;

import java.util.Arrays;
import java.util.HashSet;

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

		// GET /search (minPrice, maxPrice, maxDist, hasPool, hasWifi, hasShauna, cityName, pointX, pointY)
		router.attach("/search", RoomSearchResource.class);

		// POST /book (userId, roomId, startDate, endDate)
		router.attach("/book", BookingResource.class);

		return router;
	}




}