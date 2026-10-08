package com.travelagency.model;

public class Destination {
    private int destinationId;
    private String name;
    private String country;
    private String description;
    private String bestSeason;

    public Destination() {}

    public Destination(int destinationId, String name, String country,
                        String description, String bestSeason) {
        this.destinationId = destinationId;
        this.name = name;
        this.country = country;
        this.description = description;
        this.bestSeason = bestSeason;
    }

    public int getDestinationId() { return destinationId; }
    public void setDestinationId(int destinationId) { this.destinationId = destinationId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getBestSeason() { return bestSeason; }
    public void setBestSeason(String bestSeason) { this.bestSeason = bestSeason; }

    @Override
    public String toString() { return name + ", " + country; }
}
