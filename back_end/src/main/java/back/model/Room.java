package back.model;

import back.exceptions.JTHDataBaseException;
import back.conf.Configuration;

import java.util.List;
import java.util.Map;

public class Room {

    private int id;
    private long providerId;
    private int locationId;  // only used for updating room (can be ignored elsewhere)
    private double price;
    private int capacity;
    private boolean wifi, pool, shauna, breakfast;
    private String roomName;
    private String description;
    private Location location;
    private int maxOccupants;
    private Provider provider = null;
    private List<Rating> ratings = null;

    public Room(int id, String roomName, long providerId, int locationId, double price, int capacity, boolean wifi, boolean pool, boolean shauna, boolean breakfast, Location location, String description, int maxOccupants, Provider provider) {
        this.id = id;
        this.roomName = roomName;
        this.providerId = providerId;
        this.locationId = locationId;
        this.price = price;
        this.capacity = capacity;
        this.wifi = wifi;
        this.breakfast = breakfast;
        this.pool = pool;
        this.shauna = shauna;
        this.location = location;
        this.description = description;
        this.maxOccupants = maxOccupants;
        this.provider = provider;
    }

    public void setId(int id) {
        this.id = id;
    }

    public static Room fromMap(Map<String, Object> source) {
        //System.out.println("source:");
        //System.out.println(source);
        try {
            return new Room(
                    (int) source.get("id"),
                    (String) source.get("roomName"),
                    (int) source.get("providerId"),
                    (int) source.get("locationId"),
                    (double) source.get("price"),
                    (int) source.get("capacity"),
                    (boolean) source.get("wifi"),
                    (boolean) source.get("pool"),
                    (boolean) source.get("shauna"),
                    (boolean) source.get("breakfast"),
                    new Location((Map<String, Object>) source.get("location"),
                    (String) source.get("cityName")),
                    (String) source.get("description"),
                    (int) source.get("maxOccupants"),
                    new Provider(new User((int) source.get("providerId"), (String) source.get("email"), "provider", (Boolean) source.get("isBanned")), (String) source.get("providerName"))
            );
        } catch (Exception e){
            e.printStackTrace();
            System.err.println("WARNING: Missing parameters in JSon of SearchStorage's documents");
            return null;
        }
    }

    public int getId() {
        return id;
    }

    public long getProviderId() {
        return providerId;
    }

    public int getLocationId() {
        return locationId;
    }

    public double getPrice() {
        return price;
    }
    public int getCapacity(){
        return capacity;
    }

    public boolean getWifi() {
        return wifi;
    }

    public boolean getPool() {
        return pool;
    }

    public boolean getShauna() {
        return shauna;
    }

    public boolean getBreakfast() {
        return breakfast;
    }

    public Location getLocation() {
        return location;
    }

    public String getRoomName() {
        return roomName;
    }

    public String getDescription() {
        return description;
    }

    public int getMaxOccupants() {
        return maxOccupants;
    }

    public Provider fetchProvider(){
        if (provider == null) {
            try {
                provider = Configuration.getInstance().getRoomsDAO().getProviderForRoom(id);
                if (provider != null) providerId = provider.getId();
                else System.err.println("Warning: could not fetch provider for room with id " + id);
            } catch (JTHDataBaseException e){
                provider = null;
            }
        }
        return provider;
    }

    public List<Rating> fetchRatings(){
        if (ratings == null) {
            try {
                ratings = Configuration.getInstance().getRoomsDAO().getRatingsForRoom(id);
            } catch (JTHDataBaseException e){
                ratings = null;
            }
        }
        return ratings;
    }

    public List<Transaction> getTransactions(){
        try {
            return Configuration.getInstance().getBookingDAO().getTransactionsForRoom(id);
        } catch (JTHDataBaseException e){
            return null;
        }
    }

    public double calcCostBasedOnOccupants(int occupants){
        return price * occupants;
    }

}
