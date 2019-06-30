package back.conf;

import back.data.*;
import back.data.jdbc.*;

import java.util.Properties;
import java.util.Set;

public class Configuration {

    // set this to false for easier API testing without needing to login
    public static boolean CHECK_AUTHORISATION;

    public static final String[] CONFIG_KEYS = new String[]{"x", "y"};
    private static final Configuration self = new Configuration();

    private final DataAccess dataAccess = new DataAccess();
    private final SearchStorageImplementation search = new SearchStorageImplementation();

    private String contextPath = null;
    private Properties props = new Properties();

    private Configuration() {

    }

    public static Configuration getInstance() {
        return self;
    }

    void setup(String contextPath, Properties props) throws ConfigurationException {
        this.contextPath = contextPath;
        this.props = props;

        // true if not specified
        CHECK_AUTHORISATION = !("false".equals(getProperty("check_authorisation")));

        try {
            dataAccess.setup(
                    getProperty("db.driver"),
                    getProperty("db.url"),
                    getProperty("db.user"),
                    getProperty("db.pass")
            );
        } catch (Exception e) {
            throw new ConfigurationException(e.getMessage(), e);
        }

        // TEST
        //search.test();
    }

    public String getContextPath() {
        return contextPath;
    }

    public String getProperty(String name) {
        return getProperty(name, null);
    }

    public String getProperty(String name, String defaultValue) {
        return props.getProperty(name, defaultValue);
    }

    public Set<String> propertyNames() {
        return props.stringPropertyNames();
    }

    public UserDAO getUserDAO() {
        return new UserDAOImpl(dataAccess);
    }

    public RoomsDAO getRoomsDAO() {
        return new RoomsDAOImpl(dataAccess, search);
    }

    public BookingDAO getBookingDAO() {
        return new BookingDAOImpl(dataAccess, search);
    }

    public ImageDAO getImageDAO() {return new ImageDAOImpl(dataAccess);}

    public long getLoginTTL() {
        return 800000;
    }
}
