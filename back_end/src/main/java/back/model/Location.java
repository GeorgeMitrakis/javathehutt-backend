package back.model;


public class Location {

    private String cityname;
    private double cordX, cordY;   //TODO: make them compatible with GIS

    public void setCityname(String cityname) {
        this.cityname = cityname;
    }

    public void setCordX(String cords) {

        this.cordX = Integer.getInteger(cords.replace("POINT(", "").replace(")", "").split(" ")[0]);
    }

    public void setCordY(String cords) {
        this.cordY = Integer.getInteger(cords.replace("POINT(", "").replace(")", "").split(" ")[1]);
    }

    public double getCordX() {
        return cordX;
    }

    public double getCordY() {
        return cordY;
    }

    public String getCityname() {
        return cityname;
    }
}
