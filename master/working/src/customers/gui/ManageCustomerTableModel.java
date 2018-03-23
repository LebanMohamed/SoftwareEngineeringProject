/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package customers.gui;

import common.Database;
import common.gui.util.DefaultTableModelEx;
import customers.logic.Customer;
import customers.logic.Customer.CustomerType;
import java.sql.SQLException;
import java.util.Collection;

/**
 *
 * @author cawaala
 */
public final class ManageCustomerTableModel extends DefaultTableModelEx {

    private static final String[] COLUMN_NAMES = {"ID", "Full Name", "Phone", "Email Address", "City", "Street Address", "Postcode", "Customer Type"};
    private static final Class[] COLUMN_TYPES = {
        Integer.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class
    };

    public ManageCustomerTableModel() {
        super(COLUMN_NAMES, COLUMN_TYPES);
        setColumnEditable(0, false);

    }

    public void addRow(Customer customer) {
        addRow(new Object[]{
            customer.getId(), customer.getFullName(), customer.getPhone(), customer.getEmail(),
            customer.getCity(), customer.getStreetAddress(), customer.getPostcode(), customer.getType()
        });
    }

    public Customer rowToCustomer(int rowIdx) {
        if (rowIdx < 0 || rowIdx >= getRowCount()) {
            throw new IllegalArgumentException();
        }
        int customerId = (int) getValueAt(rowIdx, 0);
        String fullname = (String) getValueAt(rowIdx, 1);
        String phone = (String) getValueAt(rowIdx, 2);
        String emailaddress = (String) getValueAt(rowIdx, 3);
        String city = (String) getValueAt(rowIdx, 4);
        String streetaddress = (String) getValueAt(rowIdx, 5);
        String postcode = (String) getValueAt(rowIdx, 6);
        CustomerType type = (CustomerType) getValueAt(rowIdx, 7);
        return new Customer(customerId, fullname, phone, emailaddress, city, streetaddress, postcode, type);
    }

    public void loadInitialCustomerList() {
        try {
            // Load all customer in the database
            Collection<Customer> allCustomers = Database.getInstance().getAllCustomers();

            // Iterate over the customer and add them to the table row by row
            for (Customer customer : allCustomers) {
                addRow(customer);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    boolean removeRowByCustomerId(int customerId) {
        return removeRowByColumnValue(0, customerId);
    }
}
