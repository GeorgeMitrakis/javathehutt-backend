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

    public SearchConstraintsBuilder setRange(Double range){
        if (range != null) searchConstraints.setRange(range);
        return this;
    }

    public SearchConstraintsBuilder setLocation(String cityName, Double cordX, Double cordY){
        if (cityName != null && cordX != null && cordY != null) searchConstraints.setLocation(cityName, cordX, cordY);
        return this;
    }

    public SearchConstraints build(){
        return searchConstraints;
    }

}
