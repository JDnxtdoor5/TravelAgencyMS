package com.travelagency.model;

import java.math.BigDecimal;
import java.sql.Date;

public class Booking {
    private int bookingId;
    private String bookingRef;
    private int customerId;
    private String customerName;   // convenience for display
    private int packageId;
    private String packageName;    // convenience for display
    private Date travelDate;
    private int numTravelers;
    private String status;         // PENDING, CONFIRMED, CANCELLED, COMPLETED
    private BigDecimal totalAmount;

    public Booking() {}

    public Booking(int bookingId, String bookingRef, int customerId, int packageId,
                    Date travelDate, int numTravelers, String status, BigDecimal totalAmount) {
        this.bookingId = bookingId;
        this.bookingRef = bookingRef;
        this.customerId = customerId;
        this.packageId = packageId;
        this.travelDate = travelDate;
        this.numTravelers = numTravelers;
        this.status = status;
        this.totalAmount = totalAmount;
    }

    public int getBookingId() { return bookingId; }
    public void setBookingId(int bookingId) { this.bookingId = bookingId; }

    public String getBookingRef() { return bookingRef; }
    public void setBookingRef(String bookingRef) { this.bookingRef = bookingRef; }

    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public int getPackageId() { return packageId; }
    public void setPackageId(int packageId) { this.packageId = packageId; }

    public String getPackageName() { return packageName; }
    public void setPackageName(String packageName) { this.packageName = packageName; }

    public Date getTravelDate() { return travelDate; }
    public void setTravelDate(Date travelDate) { this.travelDate = travelDate; }

    public int getNumTravelers() { return numTravelers; }
    public void setNumTravelers(int numTravelers) { this.numTravelers = numTravelers; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    @Override
    public String toString() { return bookingRef + " - " + status; }
}
