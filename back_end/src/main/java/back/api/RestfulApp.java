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
		/* Full API documentation on RestAPI.md file */

		Router router = new Router(getContext());
		// POST
		router.attach("/admin", AdminResource.class);

		// POST
		router.attach("/login", LoginResource.class);

		// GET  /users             -> get User(s) objects
		// POST /users?signup=yes  -> sign up
        // PUT  /users?edit=yes    -> edit
		router.attach("/users" , UsersResource.class);

		router.attach("/dummy", DummyResource.class);

		// GET /autocomplete?str=<input>
		router.attach("/autocomplete", AutocompleteResource.class);

		// GET /search (minPrice, maxPrice, maxDist, hasPool, hasWifi, hasShauna, cityName, pointX, pointY)
		router.attach("/search", SearchResource.class);

		// POST /book (userId, roomId, startDate, endDate)
		router.attach("/book", BookingResource.class);

		// GET /rooms (roomId)    -> get room info
		// DELETE /rooms (roomId) -> delete room
		// POST /rooms            -> submit new room
		//      (providerId, roomName, price, capacity, cordX, cordY, cityName [, wifi, pool, shauna])
		router.attach("/rooms", RoomsResource.class);

		// POST (visitorId, roomId)   -> add to favorites
		// DELETE (visitorId, roomId) -> remove from favorites
        router.attach("/favourite_rooms", FavouriteRoomsResource.class);

        // GET (roomId)                              -> get ratings for room
        // POST (visitorId, roomId, stars, comment)  -> add rating
        // DELETE (ratingId)                         -> remove rating
		router.attach("/ratings", RatingResource.class);

		//GET (imgId) -> stream image
        router.attach("/img", ImageResource.class);

        //GET (roomId) -> get list of image ids associated with to roomId
        router.attach("/roomImages", RoomImagesResource.class);

		return router;
	}

}