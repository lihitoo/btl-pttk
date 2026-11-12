package model;

import java.time.LocalDateTime;

public class Reservation6 {
    private Long id;
    private Customer6 customer;
    private DiningTable6 table;
    private SalesStaff6 confirmedBy;
    private LocalDateTime reservationTime;
    private int guestCount;
    private String status;

    public Long getId() { return id; }
    public void setId(Long value) { id = value; }
    public Customer6 getCustomer() { return customer; }
    public void setCustomer(Customer6 value) { customer = value; }
    public DiningTable6 getTable() { return table; }
    public void setTable(DiningTable6 value) { table = value; }
    public SalesStaff6 getConfirmedBy() { return confirmedBy; }
    public void setConfirmedBy(SalesStaff6 value) { confirmedBy = value; }
    public LocalDateTime getReservationTime() { return reservationTime; }
    public void setReservationTime(LocalDateTime value) { reservationTime = value; }
    public int getGuestCount() { return guestCount; }
    public void setGuestCount(int value) { guestCount = value; }
    public String getStatus() { return status; }
    public void setStatus(String value) { status = value; }
}
