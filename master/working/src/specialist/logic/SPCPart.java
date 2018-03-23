/**
 * Author ec15143 Nikita Miroyubov
 */
package specialist.logic;

import java.util.Calendar;
import parts.logic.Parts;

public class SPCPart {

    private final Calendar deliveryDate;
    private final Calendar returnDate;
    private final Double repairCost;
    private final SPCBooking booking;
    private final SPC spc;
    private final Parts part;
    private int id;
    private final String faultDescription;
    private final int installedPartId;

    public SPCPart(int id, Parts part, Double repairCost, Calendar deliveryDate, Calendar returnDate, SPC spc, SPCBooking booking, String faultDescription, int partInstalledId) {
        this.id = id;
        this.deliveryDate = deliveryDate;
        this.returnDate = returnDate;
        this.repairCost = repairCost;
        this.spc = spc;
        this.booking = booking;
        this.part = part;
        this.faultDescription = faultDescription;
        this.installedPartId = partInstalledId;
    }

 
    public Calendar getDeliveryDate() {
        return deliveryDate;
    }

  
    public Calendar getReturnDate() {
        return returnDate;
    }

   
    public Double getRepairCost() {
        return repairCost;
    }

    
    public SPCBooking getSPCBooking() {
        return booking;
    }

   
    public SPC getSPC() {
        return spc;
    }

   
    public Parts getPart() {
        return part;
    }

    
    public int getInstalledPartId() {
        return installedPartId;
    }

    public int getId() {
        return id;
    }

  
    public void setID(int id) {
        this.id = id;
    }

 
    public String getFaultDescription() {
        return faultDescription;
    }

}
