/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package diagrep.gui;

import common.Database;
import common.gui.util.TableHelper;
import diagrep.gui.mechanic.ManageMechanicsFrame;
import diagrep.logic.Booking;
import diagrep.logic.BookingFilter;
import diagrep.logic.DateHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.Calendar;

/**
 * A dialog for the management and viewing of {@link diagrep.logic.Booking}s.
 */
public final class ManageBookingsDialog extends javax.swing.JDialog {

    /**
     * If past bookings should be rendered in gray text.
     */
    private static final boolean GRAY_PAST_BOOKINGS = true;
    private static final Logger logger = LoggerFactory.getLogger(ManageBookingsDialog.class);
    private final BookingsTableModel model = new BookingsTableModel();
    private javax.swing.JTable bookingsTable;
    private BookingFilter defaultFilter = BookingFilter.NO_FILTER;
    private BookingFilter activeFilter = defaultFilter;

    public ManageBookingsDialog() {
        initComponents();
        setModal(true);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the System look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        try {
            javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                ManageBookingsDialog f = new ManageBookingsDialog();
                f.setVisible(true);
            }
        });
    }

    public void setDefaultFilter(BookingFilter filter) {
        this.defaultFilter = filter;
        this.activeFilter = filter;
        reloadBookings();
    }

    private void reloadBookings() {
        long startTime = System.currentTimeMillis();
        model.clearRows();
        model.loadBookings(activeFilter);
        logger.debug("Reloaded bookings in {}ms", (System.currentTimeMillis() - startTime));
    }

    /**
     * Constructs the GUI elements of this frame.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        JScrollPane tableScrollPane = new JScrollPane();
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenuItem = new JMenu();
        JMenuItem addMenuItem = new JMenuItem();
        JMenuItem editMenuItem = new JMenuItem();
        JMenuItem removeMenuItem = new JMenuItem();
        JMenuItem mechanicsMenuItem = new JMenuItem();
        JMenuItem viewMenuItem = new JMenuItem();
        JMenuItem chartsMenuItem = new JMenuItem();
        JMenuItem searchMenuItem = new JMenuItem();
        JMenuItem resetSearchMenuItem = new JMenuItem();
        JMenuItem doneMenuItem = new JMenuItem();
        bookingsTable = new javax.swing.JTable() {

            /**
             * Renderer strategy. Draw rows which are for past bookings with a gray coloured font.
             */
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);

                if (GRAY_PAST_BOOKINGS) {
                    Calendar endDate = DateHelper.toCalendar((String) model.getValueAt(row, 2));

                    if (Calendar.getInstance().compareTo(endDate) < 0) {
                        c.setForeground(Color.black);
                    } else {
                        c.setForeground(Color.gray);
                    }
                }
                return c;
            }
        };

        setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        setTitle("GMSIS / Manage Bookings");
        setPreferredSize(new java.awt.Dimension(1072, 500));
        setMinimumSize(new java.awt.Dimension(800, 350));

        bookingsTable.setAutoCreateRowSorter(true);
        bookingsTable.setModel(model);
        bookingsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        model.loadBookings(defaultFilter);
        tableScrollPane.setViewportView(bookingsTable);

        // Set table column styles
        TableHelper.leftAlignColumnValues(bookingsTable, 0);
        TableHelper.leftAlignColumnValues(bookingsTable, 4);
        TableHelper.setColumnPrefWidth(bookingsTable, 0, 6);
        TableHelper.setColumnPrefWidth(bookingsTable, 1, 80);
        TableHelper.setColumnPrefWidth(bookingsTable, 2, 80);
        TableHelper.setColumnPrefWidth(bookingsTable, 3, 90);
        TableHelper.setColumnPrefWidth(bookingsTable, 4, 40);
        TableHelper.setColumnPrefWidth(bookingsTable, 7, 40);
        TableHelper.setColumnPrefWidth(bookingsTable, 8, 40);
        TableHelper.setColumnPrefWidth(bookingsTable, 9, 40);

        fileMenuItem.setText("File");

        addMenuItem.setText("Add...");
        addMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                addMenuItemActionPerformed();
            }
        });
        addMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N,
                Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        fileMenuItem.add(addMenuItem);

        editMenuItem.setText("Edit...");
        editMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                editMenuItemActionPerformed();
            }
        });
        editMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E,
                Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        fileMenuItem.add(editMenuItem);

        removeMenuItem.setText("Remove");
        removeMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                removeMenuItemActionPerformed();
            }
        });
        removeMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D,
                Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        fileMenuItem.add(removeMenuItem);

        mechanicsMenuItem.setText("Mechanics...");
        mechanicsMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                mechanicsMenuItemActionPerformed();
            }
        });
        fileMenuItem.add(mechanicsMenuItem);
        fileMenuItem.add(new JPopupMenu.Separator());

        viewMenuItem.setText("View");
        viewMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                viewMenuItemActionPerformed();
            }
        });
        fileMenuItem.add(viewMenuItem);

        chartsMenuItem.setText("Charts");
        chartsMenuItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                chartsMenuItemActionPerformed();
            }
        });
        fileMenuItem.add(chartsMenuItem);

        searchMenuItem.setText("Search...");
        searchMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                searchMenuItemActionPerformed();
            }
        });
        searchMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
                Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        fileMenuItem.add(searchMenuItem);

        resetSearchMenuItem.setText("Reset Search");
        resetSearchMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                resetSearchMenuItemActionPerformed();
            }
        });
        resetSearchMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z,
                Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        fileMenuItem.add(resetSearchMenuItem);
        fileMenuItem.add(new JPopupMenu.Separator());

        doneMenuItem.setText("Done");
        doneMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                doneMenuItemActionPerformed();
            }
        });
        fileMenuItem.add(doneMenuItem);

        menuBar.add(fileMenuItem);

        setJMenuBar(menuBar);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(tableScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 700, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(tableScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 449, Short.MAX_VALUE)
        );
        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void chartsMenuItemActionPerformed() {
        ChartsFrame frame = new ChartsFrame();

        if (!frame.isErrorOccurred()) {
            frame.setVisible(true);
        }
    }

    private void addMenuItemActionPerformed() {
        AddBookingPrompt prompt = new AddBookingPrompt();
        prompt.show();

        if (prompt.isSuccessful()) { // Reload table
            reloadBookings();
        }
    }

    private void editMenuItemActionPerformed() {
        // Get selected row in table
        int rowNo = bookingsTable.getSelectedRow();

        if (rowNo == -1) {
            JOptionPane.showMessageDialog(this, "No row was selected.", "Edit Booking - Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        rowNo = bookingsTable.convertRowIndexToModel(rowNo); // Source: https://stackoverflow.com/a/2075482
        Booking booking = model.rowToBooking(rowNo);

        // Show edit booking
        EditBookingPrompt prompt = new EditBookingPrompt(booking);
        prompt.show();

        if (prompt.isSuccessful()) { // Reload table
            reloadBookings();
        }
    }

    private void removeMenuItemActionPerformed() {
        // Get selected row in table
        int rowNo = bookingsTable.getSelectedRow();
        rowNo = bookingsTable.convertRowIndexToModel(rowNo);

        if (rowNo == -1) {
            JOptionPane.showMessageDialog(this, "No row was selected.", "Delete Booking", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int bookingId = (int) model.getValueAt(rowNo, 0);
        Booking booking = model.rowToBooking(rowNo);

        // Ask to confirm action
        if (JOptionPane.showConfirmDialog(this, "Are you sure you want to delete the booking with id: '" + bookingId
                + "'", "Confirm deletion of booking", JOptionPane.YES_NO_OPTION)
                != JOptionPane.YES_OPTION)
            return;

        try {
            // Deleting booking from the database and table
            Database.getInstance().removeBooking(booking);
            model.removeRowByBookingId(bookingId);

            JOptionPane.showMessageDialog(this, "Successfully deleted booking from the database.", "Delete Booking",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            logger.debug("Stack Trace from removeMenuItemActionPerformed");

            for (StackTraceElement el : e.getStackTrace()) {
                logger.debug(el.toString());
            }

            JOptionPane.showMessageDialog(this, "Error deleting booking from the database.\r\n"
                    + "Error Message: " + e.getMessage(), "Delete Booking - Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void viewMenuItemActionPerformed() {
        // Get selected row in table
        int rowNo = bookingsTable.getSelectedRow();

        if (rowNo == -1) {
            JOptionPane.showMessageDialog(this, "No row was selected.", "View Booking - Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        rowNo = bookingsTable.convertRowIndexToModel(rowNo);
        Booking booking = model.rowToBooking(rowNo);

        // Show view booking
        ViewBookingPrompt prompt = new ViewBookingPrompt(booking);
        prompt.show();
    }

    private void searchMenuItemActionPerformed() {
        SearchBookingsPrompt sbp = new SearchBookingsPrompt();
        sbp.show();

        if (sbp.isSuccessful()) {
            activeFilter = sbp.parseBookingFilter();
            reloadBookings();
        }
    }

    private void resetSearchMenuItemActionPerformed() {
        activeFilter = defaultFilter;
        reloadBookings();
    }

    private void doneMenuItemActionPerformed() {
        setVisible(false);
        dispose();
    }

    private void mechanicsMenuItemActionPerformed() {
        new ManageMechanicsFrame().setVisible(true);

        // Reload bookings so mechanics changes take place
        reloadBookings();
    }
}
