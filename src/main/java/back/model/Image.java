package back.model;

public class Image {
    private long id;
    private String url;
    private long roomId;

    public long getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }

    public long getRoomId() {
        return roomId;
    }



    public Image(long id, String url, long roomId) {
        this.id = id;
        this.url = url;
        this.roomId = roomId;
    }
}
