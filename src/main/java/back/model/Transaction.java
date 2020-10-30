package back.model;

public class Transaction {

    private long visitorId;
    private int roomId;
    private int id;
    private String startDate;
    private String endDate;
    private double cost;
    private int occupants;


    public Transaction(long visitorId, int roomId, int id, String startDate, String endDate, double cost, int occupants) {
        this.visitorId = visitorId;
        this.roomId = roomId;
        this.id = id;
        this.startDate = startDate;
        this.endDate = endDate;
        this.cost = cost;
        this.occupants = occupants;
    }

    public long getVisitorId() {
        return visitorId;
    }

    public int getRoomId() {
        return roomId;
    }

    public int getId() {
        return id;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public double getCost() {
        return cost;
    }

    public int getOccupants() {
        return occupants;
    }

}
