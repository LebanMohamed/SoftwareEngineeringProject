package diagrep.logic;

import common.logic.Invoice;
import customers.logic.Customer;
import vehicles.logic.Vehicle;

import java.util.Calendar;
import java.util.List;

/**
 * A booking.
 */
public abstract class Booking {

    private int bookingId;
    private Invoice invoice;
    private final Calendar bookingStartDate;
    private final Calendar bookingEndDate;
    private Mechanic mechanic;
    private Calendar mechanicTimeSpent;
    private Customer customer;
    private Vehicle vehicle;

    Booking(int bookingId, Calendar bookingStartDate, Calendar bookingEndDate) {
        this.bookingId = bookingId;
        this.bookingStartDate = bookingStartDate;
        this.bookingEndDate = bookingEndDate;
    }

    public int getId() {
        return bookingId;
    }

    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
    }

    public Calendar getBookingStartDate() {
        return this.bookingStartDate;
    }

    public Calendar getBookingEndDate() {
        return this.bookingEndDate;
    }

    public Customer getCustomer() {
        return this.customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Vehicle getVehicle() {
        return this.vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public Invoice getInvoice() {
        return this.invoice;
    }

    public void setInvoice(Invoice invoice) {
        this.invoice = invoice;
    }

    public Mechanic getMechanic() {
        return mechanic;
    }

    public void setMechanic(Mechanic mechanic) {
        this.mechanic = mechanic;
    }

    public Calendar getMechanicTimeSpent() {
        return mechanicTimeSpent;
    }

    public void setMechanicTimeSpent(Calendar mechanicTimeSpent) {
        this.mechanicTimeSpent = mechanicTimeSpent;
    }

    /**
     * Un-rounded value, 0 if a mechanic is not set (is null).
     */
    public double mechanicCost() {
        return mechanic == null ? 0 : DateHelper.toFractionalHours(mechanicTimeSpent) * mechanic.getHourlyWage();
    }

    public static String listIds(List<Booking> bookings) {
        final int n = bookings.size();
        final StringBuilder sb = new StringBuilder();
        sb.append("[");

        for (int i = 0; i < n; i++) {
            Booking booking = bookings.get(i);
            sb.append(booking.getId());

            if (i + 1 < n)
                sb.append(", ");
        }
        return sb.append("]").toString();
    }
}