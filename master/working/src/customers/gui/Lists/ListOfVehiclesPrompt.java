package customers.gui.Lists;

 
import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.text.ParseException;

/**
 *
 * @author cawaala
 */
public final class ListOfVehiclesPrompt {

    private final ListOfVehiclesTableModel tableModel = new ListOfVehiclesTableModel();

    public void show() throws ParseException, SQLException {
        tableModel.loadVehicles();
        // Design prompt
        JScrollPane scrollPane = new JScrollPane();
        JTable table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        scrollPane.setViewportView(table);
        scrollPane.setPreferredSize(new Dimension(800, 400));

        //TableHelper.leftAlignColumnValues(table, 4);
        //TableHelper.leftAlignColumnValues(table, 7);

        // Show prompt
        int result = JOptionPane.showConfirmDialog(null, scrollPane, "GMSIS / Vehicles  ",
                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE);
    }
}
