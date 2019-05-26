package back.model;


public class Location {

    private String cityname;
    private double cordX, cordY;

    public Location(String cityname, double cordX, double cordY){
        this.setCityname(cityname);
        this.cordX = cordX;
        this.cordY = cordY;
    }

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

    public String getCoords(){
        return "POINT(".concat(Double.toString(cordX)).concat(" ").concat(Double.toString(cordY)).concat(")");
    }
}
