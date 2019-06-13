package back.model;

public class Transaction {

    private int visitorId;
    private int roomId;
    private int id;
    private String startDate;
    private String endDate;
    private double cost;


    public Transaction(int visitorId, int roomId, int id, String startDate, String endDate, double cost) {
        this.visitorId = visitorId;
        this.roomId = roomId;
        this.id = id;
        this.startDate = startDate;
        this.endDate = endDate;
        this.cost = cost;
    }

    public int getVisitorId() {
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
}
