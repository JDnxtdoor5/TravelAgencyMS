package com.travelagency.model;

import java.math.BigDecimal;
import java.sql.Date;

public class Payment {
    private int paymentId;
    private int bookingId;
    private String bookingRef;     // convenience for display
    private BigDecimal amountPaid;
    private Date paymentDate;
    private String paymentMethod;  // CASH, CARD, BANK_TRANSFER, GCASH, OTHER
    private String status;         // PAID, PARTIAL, REFUNDED

    public Payment() {}

    public Payment(int paymentId, int bookingId, BigDecimal amountPaid, Date paymentDate,
                    String paymentMethod, String status) {
        this.paymentId = paymentId;
        this.bookingId = bookingId;
        this.amountPaid = amountPaid;
        this.paymentDate = paymentDate;
        this.paymentMethod = paymentMethod;
        this.status = status;
    }

    public int getPaymentId() { return paymentId; }
    public void setPaymentId(int paymentId) { this.paymentId = paymentId; }

    public int getBookingId() { return bookingId; }
    public void setBookingId(int bookingId) { this.bookingId = bookingId; }

    public String getBookingRef() { return bookingRef; }
    public void setBookingRef(String bookingRef) { this.bookingRef = bookingRef; }

    public BigDecimal getAmountPaid() { return amountPaid; }
    public void setAmountPaid(BigDecimal amountPaid) { this.amountPaid = amountPaid; }

    public Date getPaymentDate() { return paymentDate; }
    public void setPaymentDate(Date paymentDate) { this.paymentDate = paymentDate; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() { return "Payment #" + paymentId + " - " + status; }
}
