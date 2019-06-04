package back.model;

import back.Exceptions.JTHDataBaseException;
import back.conf.Configuration;

import java.util.List;

public class Room {

    private int id;
    private long providerId;
    private double price;
    private int capacity;
    private boolean wifi, pool, shauna;
    private String roomName;
    private String description = "";   // TODO: add to db and here
    private Location location;

    private Provider provider = null;
    private List<Rating> ratings = null;

    public Room(int id, String roomName, long providerId, double price, int capacity, boolean wifi, boolean pool, boolean shauna, Location location, String description) {
        this.id = id;
        this.roomName = roomName;
        this.providerId = providerId;
        this.price = price;
        this.capacity = capacity;
        this.wifi = wifi;
        this.pool = pool;
        this.shauna = shauna;
        this.location = location;
        this.description = description;
    }

    public long getId() {
        return id;
    }

    public long getProviderId() {
        return providerId;
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

    public Provider fetchProvider(){
        if (provider == null) {
            try {
                provider = Configuration.getInstance().getRoomsDAO().getProviderForRoom(id);
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
