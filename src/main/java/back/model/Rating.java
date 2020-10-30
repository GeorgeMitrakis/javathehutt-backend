package back.model;

public class Rating {

    private int id, roomId;
    private long visitorId;
    private int stars;
    private String comment;

    public Rating(int id, int roomId, long visitorId, int stars, String comment){
        this.id = id;
        this.roomId = roomId;
        this.visitorId = visitorId;
        this.stars = stars;
        this.comment = comment;
    }

    public int getId() {
        return id;
    }

    public int getRoomId() {
        return roomId;
    }

    public long getVisitorId() {
        return visitorId;
    }

    public int getStars() {
        return stars;
    }

    public String getComment() {
        return comment;
    }

}
