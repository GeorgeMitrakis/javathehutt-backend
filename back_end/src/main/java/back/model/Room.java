package back.model;

public class Room {

    private long id;
    private long providerId;
    private double price;
    private int capacity;
    private Provider provider = null;

    public Room(long id, long providerId, double price, int capacity) {
        this.id = id;
        this.providerId = providerId;
        this.price = price;
        this.capacity = capacity;
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

    public int getCapacity(){ return capacity;}

    public Provider fetchProvider(){
        if (provider == null) {
            // TODO: fetch provider with providerId from db
            provider = /* TODO */ null;
        }
        return provider;
    }

}
