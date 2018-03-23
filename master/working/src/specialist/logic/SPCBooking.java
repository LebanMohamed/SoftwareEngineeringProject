/**
 * Author ec15143 Nikita Miroyubov
 */
package specialist.logic;

import diagrep.logic.Booking;
import java.util.Calendar;

public class SPCBooking {

    private Calendar startDate;
    private Calendar endDate;
    private double SPCBookingTotal;
    private final Booking booking;
    private int spcBookingId;

    public SPCBooking(int spcBookingId, Booking booking) {
        this.booking = booking;
        this.spcBookingId = spcBookingId;
    }

    public Calendar getStartDate() {
        return startDate;
    }

    public void setStartDate(Calendar startDate) {
        this.startDate = startDate;
    }

    public Calendar getEndDate() {
        return endDate;
    }

    public void setEndDate(Calendar endDate) {
        this.endDate = endDate;
    }

    public double getSPCBookingTotal() {
        return SPCBookingTotal;
    }

    public void setSPCBookingTotal(double BookingTotal) {
        this.SPCBookingTotal = BookingTotal;
    }


    public Booking getBooking() {
        return booking;
    }

    public int getSpcBookingId() {
        return spcBookingId;
    }

  
    public void setSpcBookingId(int spcBookingId) {
        this.spcBookingId = spcBookingId;
    }

    public void setTotalCost(Double cost) {
        this.SPCBookingTotal = cost;
    }
}
