package back.api;

import back.conf.Configuration;
import back.data.BookingDAO;
import org.restlet.resource.ServerResource;


public class BookingResource  extends ServerResource {

    private final BookingDAO bookingDAO = Configuration.getInstance().getBookingDAO();


    //TODO

}
