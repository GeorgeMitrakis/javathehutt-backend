package back.model;


public class SearchConstraintsBuilder {

    private SearchConstraints searchConstraints;

    public SearchConstraintsBuilder(){
        searchConstraints = new SearchConstraints();
    }

    public SearchConstraintsBuilder setMinCost(Integer minCost){
        if (minCost != null) searchConstraints.setMinCost(minCost);
        return this;
    }

    public SearchConstraintsBuilder setMaxCost(Integer maxCost){
        if (maxCost != null) searchConstraints.setMaxCost(maxCost);
        return this;
    }

    public SearchConstraintsBuilder setWifi(Boolean wifi){
        if (wifi != null) searchConstraints.setWifi(wifi);
        return this;
    }

    public SearchConstraintsBuilder setPool(Boolean pool){
        if (pool != null) searchConstraints.setPool(pool);
        return this;
    }

    public SearchConstraintsBuilder setShauna(Boolean shauna){
        if (shauna != null) searchConstraints.setShauna(shauna);
        return this;
    }

    public SearchConstraintsBuilder setBreakfast(Boolean breakfast){
        if (breakfast != null) searchConstraints.setBreakfast(breakfast);
        return this;
    }

    public SearchConstraintsBuilder setRange(Double range){
        if (range != null) searchConstraints.setRange(range);
        return this;
    }

    public SearchConstraintsBuilder setLocation(String cityName, Double cordX, Double cordY){
        if ((cordX != null && cordY != null) || cityName != null) searchConstraints.setLocation(cityName, cordX, cordY);
        return this;
    }

    public SearchConstraintsBuilder setOccupants(Integer occupants){
        if (occupants != null) searchConstraints.setOccupants(occupants);
        return this;
    }

    public SearchConstraintsBuilder setDescription(String description) {
        if (description != null) searchConstraints.setDescription(description);
        return this;
    }

    public SearchConstraintsBuilder setStartDate(String startDate){
        if (startDate != null) searchConstraints.setStartDate(startDate);
        return this;
    }

    public SearchConstraintsBuilder setEndDate(String endDate){
        if (endDate != null) searchConstraints.setEndDate(endDate);
        return this;
    }

    public SearchConstraints build(){
        return searchConstraints;
    }
}
