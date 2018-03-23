package diagrep.logic;

import java.util.Calendar;

public final class DiagnosisRepairBooking extends Booking {

    private int vehicleMileage;

    public DiagnosisRepairBooking(int bookingId, Calendar bookingStartDate, Calendar bookingEndDate) {
        super(bookingId, bookingStartDate, bookingEndDate);
    }

    public int getVehicleMileage() {
        return this.vehicleMileage;
    }

    public void setVehicleMileage(int vehicleMileage) {
        this.vehicleMileage = vehicleMileage;
    }
}
