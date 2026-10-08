package com.travelagency.model;

import java.math.BigDecimal;

/**
 * Named TravelPackage (not "Package") because "package" is a reserved
 * Java keyword.
 */
public class TravelPackage {
    private int packageId;
    private String packageName;
    private int destinationId;
    private String destinationName; // convenience field for display in tables
    private int durationDays;
    private BigDecimal price;
    private String inclusions;
    private boolean active;

    public TravelPackage() {}

    public TravelPackage(int packageId, String packageName, int destinationId,
                          int durationDays, BigDecimal price, String inclusions, boolean active) {
        this.packageId = packageId;
        this.packageName = packageName;
        this.destinationId = destinationId;
        this.durationDays = durationDays;
        this.price = price;
        this.inclusions = inclusions;
        this.active = active;
    }

    public int getPackageId() { return packageId; }
    public void setPackageId(int packageId) { this.packageId = packageId; }

    public String getPackageName() { return packageName; }
    public void setPackageName(String packageName) { this.packageName = packageName; }

    public int getDestinationId() { return destinationId; }
    public void setDestinationId(int destinationId) { this.destinationId = destinationId; }

    public String getDestinationName() { return destinationName; }
    public void setDestinationName(String destinationName) { this.destinationName = destinationName; }

    public int getDurationDays() { return durationDays; }
    public void setDurationDays(int durationDays) { this.durationDays = durationDays; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public String getInclusions() { return inclusions; }
    public void setInclusions(String inclusions) { this.inclusions = inclusions; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    @Override
    public String toString() { return packageName + " (" + durationDays + " days)"; }
}
