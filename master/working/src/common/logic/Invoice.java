package common.logic;

/**
 * The invoice associated with a {@link diagrep.logic.Booking} and potentially an {@link specialist.logic.SPCBooking}.
 */
public class Invoice {

    /**
     * The invoice id, this is unique.
     */
    private int id;
    /**
     * If the invoice was paid for in full.
     */
    private boolean settled;
    private boolean originalSettled;
    private double amtPaid;
    /**
     * A fixed cost associated with each booking.
     */
    private double bookingBaseCost;
    private double bookingTotalCost;
    private double bookingAmountDue;
    private double spcTotalCost;
    private double spcAmountDue;
    /**
     * If the vehicle for which this invoice belongs to has an active warranty, in which case the invoice is considered
     * settled without any payment being made necessary.
     */
    private boolean vehicleWarrantyActive;

    public Invoice() {
        this(-1, 0, 0, 0, 0, 0, 0, false, false);
    }

    public Invoice(int id, double amtPaid, double bookingBaseCost, double bookingTotalCost, double bookingAmountDue,
                   double spcTotalCost, double spcAmountDue, boolean settled, boolean vehicleWarrantyActive) {
        this.id = id;
        this.amtPaid = amtPaid;
        this.bookingBaseCost = bookingBaseCost;
        this.settled = originalSettled = settled;
        this.vehicleWarrantyActive = vehicleWarrantyActive;
        this.bookingTotalCost = bookingTotalCost;
        this.bookingAmountDue = bookingAmountDue;
        this.spcTotalCost = spcTotalCost;
        this.spcAmountDue = spcAmountDue;

        // Handle vehicle warranty
        if (vehicleWarrantyActive) {
            activateVehicleWarranty();
        }
    }

    public boolean isSettled() {
        return this.settled;
    }

    public void setSettled(boolean settled) {
        this.settled = settled;
    }

    /**
     * @return Sum of {@link #spcTotalCost} and {@link #bookingTotalCost}.
     */
    public double getTotalCost() {
        return spcTotalCost + bookingTotalCost;
    }

    /**
     * @return Sum of {@link #spcAmountDue} and {@link #bookingAmountDue}.
     */
    public double getTotalAmountDue() {
        return bookingAmountDue + spcAmountDue;
    }

    public double getAmtPaid() {
        return amtPaid;
    }

    public void setAmtPaid(double amtPaid) {
        this.amtPaid = amtPaid;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getBookingBaseCost() {
        return bookingBaseCost;
    }

    public void setBookingBaseCost(double bookingBaseCost) {
        this.bookingBaseCost = bookingBaseCost;
    }

    public boolean isVehicleWarrantyActive() {
        return vehicleWarrantyActive;
    }

    public double getBookingTotalCost() {
        return bookingTotalCost;
    }

    public void setBookingTotalCost(double bookingTotalCost) {
        this.bookingTotalCost = bookingTotalCost;
    }

    public double getBookingAmountDue() {
        return bookingAmountDue;
    }

    public void setBookingAmountDue(double bookingAmountDue) {
        this.bookingAmountDue = bookingAmountDue;
    }

    public double getSpcTotalCost() {
        return spcTotalCost;
    }

    public void setSpcTotalCost(double spcTotalCost) {
        this.spcTotalCost = spcTotalCost;
    }

    public double getSpcAmountDue() {
        return spcAmountDue;
    }

    public void setSpcAmountDue(double spcAmountDue) {
        this.spcAmountDue = spcAmountDue;
    }

    public boolean isOriginalSettled() {
        return originalSettled;
    }

    /**
     * Marks this invoice as having a valid vehicle warranty, as such, it is considered settled.
     */
    public void activateVehicleWarranty() {
        vehicleWarrantyActive = true;
        settled = true;
        bookingAmountDue = spcAmountDue = 0;
    }
     public void deactivateVehicleWarranty() {
        vehicleWarrantyActive = false;
    }
}