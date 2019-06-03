package back.model;

public class Room {

    private long id;
    private long providerId;
    private double price;
    private int capacity;
    private boolean wifi, pool, shauna;
    private String description = "";
    private Provider provider = null;
    private Location location;

    public Room(long id, long providerId, double price, int capacity, boolean wifi, boolean pool, boolean shauna, Location location) {
        this.id = id;
        this.providerId = providerId;
        this.price = price;
        this.capacity = capacity;
        this.wifi = wifi;
        this.pool = pool;
        this.shauna = shauna;
        this.location = location;
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
            // TODO: fetch provider with providerId from db
            provider = /* TODO */ null;
        }
        return provider;
    }

}
