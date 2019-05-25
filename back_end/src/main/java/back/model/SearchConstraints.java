package back.model;

public class SearchConstraints {

    // a class that holds search constrains to be applied in room search
    private Location location = null;
    private int minCost = -1, maxCost = -1;
    private double range = -1.0;          // in kms

    // Getters and Setters
    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
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
