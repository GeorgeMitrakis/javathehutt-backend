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

		// GET /autocomplete?str=<input>
		router.attach("/autocomplete", AutocompleteResource.class);

		// GET /search (minPrice, maxPrice, maxDist, hasPool, hasWifi, hasShauna, cityName, pointX, pointY)
		//--data 'minPrice=-1&maxPrice=-1&maxDist=-1&hasPool=false&hasWifi=false&hasShauna=false&Name=Athens&pointX=-1&pointY=-1'
		//--data 'minPrice=-1&maxPrice=-1&maxDist=100&hasPool=false&hasWifi=false&hasShauna=false&Name=Athens&pointX=37.983810&pointY=23.727539'
		router.attach("/search", RoomSearchResource.class);

		// POST /book (userId, roomId, startDate, endDate)
		router.attach("/book", BookingResource.class);

		// GET /rooms (roomId)    -> get room info
		// DELETE /rooms (roomId) -> delete room
		// POST /rooms            -> submit new room
		//      (providerId, price, capacity, cordX, cordY, cityName [, wifi, pool, shauna])
		router.attach("/rooms", RoomsResource.class);

		router.attach("/img", ImageResource.class);
		return router;
	}




}