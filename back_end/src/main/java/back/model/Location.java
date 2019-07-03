package back.model;


import java.util.Map;

public class Location {

    private String cityname;
    private Double cordX, cordY;

    public Location(String cityname, Double cordX, Double cordY){
        this.setCityname(cityname);
        this.cordX = cordX;
        this.cordY = cordY;
    }

    public Location(Map<String,Object> location, String cityname) {
        cordX = (double) location.get("lat");
        cordY = (double) location.get("lon");
        this.cityname = cityname;
    }

    public void setCityname(String cityname) {
        this.cityname = cityname;
    }

    public double getCordX() {
        return (cordX != null) ? cordX : 0.0;
    }

    public double getCordY() {
        return (cordY != null) ? cordY : 0.0;
    }

    public boolean hasCords(){
        return cordX != null && cordY != null;
    }

    public String getCityname() {
        return cityname;
    }

    public String getCoords(){
        return "POINT(".concat(Double.toString(cordX)).concat(" ").concat(Double.toString(cordY)).concat(")");
    }
}
