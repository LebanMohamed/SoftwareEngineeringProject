package customers.gui.Search;


import vehicles.logic.Vehicle;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList; 

/**
 *
 * @author cawaala
 */
public final class SearchVehiclePrompt{ 

    ArrayList<Vehicle> list; 
    private final SearchVehiclesTableModel tableModel = new SearchVehiclesTableModel();
    private int selectedRowIdx;

    public SearchVehiclePrompt(ArrayList<Vehicle> list) {
         this.list = list;
    }
    public void show() { 
        tableModel.loadAllVehiclesPerCustomer(list);
        // Design prompt
        JScrollPane scrollPane = new JScrollPane();
        JTable table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        scrollPane.setViewportView(table);
        scrollPane.setPreferredSize(new Dimension(800, 400));

        //TableHelper.leftAlignColumnValues(table, 4);
        //TableHelper.leftAlignColumnValues(table, 7);

        // Show prompt
        int result = JOptionPane.showConfirmDialog(null, scrollPane, "GMSIS / Searched Vehicles",
                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE);
    }
}
