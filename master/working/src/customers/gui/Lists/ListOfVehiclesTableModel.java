/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package customers.gui.Lists;

import common.Database;
import common.gui.util.DefaultTableModelEx; 
import diagrep.logic.DateHelper;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList; 
import vehicles.logic.Vehicle;

/**
 *
 * @author cawaala
 */
public class ListOfVehiclesTableModel extends DefaultTableModelEx {

    private static final String[] COLUMN_NAMES = {"Customer Name", "Registration No.", "Colour", "MOT Renewal Date", "Warranty",
        "Current Mileage", "Make", "Model", "Engine Size", "Fuel Type"};
    private static final Class[] COLUMN_TYPES = {
        String.class, String.class, String.class, String.class, Boolean.class, String.class, String.class,
        String.class, String.class, String.class 
    };

    public ListOfVehiclesTableModel() {
        super(COLUMN_NAMES, COLUMN_TYPES);
        setEditable(false);
    }

    public void addRow(Vehicle vehicle) {
        addRow(new Object[]{
            vehicle.getCustomer().getFullName(), vehicle.getRegistrationNo(), vehicle.getColour(), DateHelper.toString(vehicle.getMOTRenewalDate()),
            vehicle.isWarrantyActive(), vehicle.getCurrentMileage(), vehicle.getTemplate().getMake(),
            vehicle.getTemplate().getModel(), vehicle.getTemplate().getEngineSize(), vehicle.getTemplate().getFuelType()
        });
    }

    public void loadVehicles() {
        try {
            ArrayList<Vehicle> Vehicles = Database.getInstance().getAllVehicles();
            for (Vehicle v : Vehicles) {
                addRow(v);
            }
        } catch (SQLException | ParseException e) {
            e.printStackTrace();
        }
    }
}
