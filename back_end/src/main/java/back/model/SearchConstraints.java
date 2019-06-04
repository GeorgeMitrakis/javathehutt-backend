package back.model;

public class SearchConstraints {

    // a class that holds search constrains to be applied in room search
    private Location location = null;
    private int minCost = -1, maxCost = -1;
    private boolean wifi = false;
    private boolean pool = false;
    private boolean shauna = false;
    private double range = -1.0;          // in kms
    private int people = 1;               // 1 is the default

    // Setters and Getters
    public int getPeople() {
        return people;
    }

    public void setPeople(int people) {
        this.people = people;
    }

    public boolean getWifi() {
        return wifi;
    }

    public void setWifi(boolean wifi) {
        this.wifi = wifi;
    }

    public boolean getPool() {
        return pool;
    }

    public void setPool(boolean pool) {
        this.pool = pool;
    }

    public boolean getShauna() {
        return shauna;
    }

    public void setShauna(boolean shauna) {
        this.shauna = shauna;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(String cityname, double cordX, double cordY) {
        this.location = new Location(cityname, cordX, cordY);
    }

    public int getMinCost() {
        return minCost;
    }

    public void setMinCost(int minCost) {
        this.minCost = minCost;
    }

    public int getMaxCost() {
        return maxCost;
    }

    public void setMaxCost(int maxCost) {
        this.maxCost = maxCost;
    }

    public double getRange() {
        return range;
    }

    public void setRange(double range) {
        this.range = range;
    }

}
