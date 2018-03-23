/**
 * Author ec15143 Nikita Miroyubov
 */
package specialist.logic;

import java.util.Calendar;

public class SPCVehicle {

    private final Calendar expDeliveryDate;
    private final Calendar expReturnDate;
    private final double repairCost;
    private final SPCBooking booking;
    private final SPC spc;
    private int id;

    public SPCVehicle(Calendar expDeliveryDate, Calendar expReturnDate, double repairCost, SPCBooking booking, SPC spc, int id) {

        this.booking = booking;
        this.expDeliveryDate = expDeliveryDate;
        this.expReturnDate = expReturnDate;
        this.repairCost = repairCost;
        this.spc = spc;
        this.id = id;
    }

    
    public Calendar getExpDeliveryDate() {
        return expDeliveryDate;
    }

   
    public Calendar getExpReturnDate() {
        return expReturnDate;
    }

   
    public double getRepairCost() {
        return repairCost;
    }

    
    public SPCBooking getSPCBooking() {
        return booking;
    }

   
    public SPC getSPC() {
        return spc;
    }

    public void setID(int id) {
        this.id = id;
    }

    
    public int getId() {
        return id;
    }

}
