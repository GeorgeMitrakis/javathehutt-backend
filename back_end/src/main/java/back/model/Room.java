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
    private boolean wifi, pool, shauna;
    private String roomName;
    private String description;
    private Location location;
    private int maxOccupants;

    private Provider provider = null;
    private List<Rating> ratings = null;

    public Room(int id, String roomName, long providerId, int locationId, double price, int capacity, boolean wifi, boolean pool, boolean shauna, Location location, String description, int maxOccupants, boolean fetchExtraFromDB) {
        this.id = id;
        this.roomName = roomName;
        this.providerId = providerId;
        this.locationId = locationId;
        this.price = price;
        this.capacity = capacity;
        this.wifi = wifi;
        this.pool = pool;
        this.shauna = shauna;
        this.location = location;
        this.description = description;
        this.maxOccupants = maxOccupants;
        if (fetchExtraFromDB) {
            fetchProvider();
            fetchRatings();
        }
    }

    public void setId(int id) {
        this.id = id;
    }

    public static Map fromMap(Map<String, Object> source) {
        return null;
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

}
