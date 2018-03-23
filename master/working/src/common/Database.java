package common;

import common.logic.Invoice;
import common.logic.User;
import common.util.MathHelper;
import customers.logic.Customer;
import customers.logic.Customer.CustomerType;
import diagrep.logic.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import parts.logic.*;
import specialist.logic.*;
import vehicles.logic.DateHelperVeh;
import vehicles.logic.Vehicle;
import vehicles.logic.VehicleTemplate;
import vehicles.logic.VehicleTemplate.VehicleType;
import vehicles.logic.Warranty;

import java.sql.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;


/**
 * Database access object. Source: https://www.sqlite.org/datatype3.html Source:
 * http://qmplus.qmul.ac.uk/mod/resource/view.php?id=489935 (Mustafa Bozkurt's
 * template class for Database)
 */
public final class Database {

    private static final boolean LOAD_AS_RESOURCE = false;
    private static final String DATABASE_FILE_NAME = "database.db";
    /**
     * Singleton instance of this class.
     */
    private static final Database INSTANCE = new Database(DATABASE_FILE_NAME);
    /**
     * {@link PreparedStatement} query time-out (in seconds).
     */
    private static final int QUERY_TIMEOUT = 5;
    /**
     * Logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(Database.class);
    /**
     * The underlying database connection.
     */
    private final Connection connection;

    private Database(String dbFileName) {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:" + (LOAD_AS_RESOURCE ? ":resource:" : "") + dbFileName);
            connection.setAutoCommit(true);
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new RuntimeException("Database connection failed!", ex);
        }
    }

    public static Database getInstance() {
        return INSTANCE;
    }

    /**
     * Creates a pre-configured {@link PreparedStatement} from the underlying
     * connection.
     */
    private PreparedStatement query(String sql) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setQueryTimeout(QUERY_TIMEOUT);
        return preparedStatement;
    }

    /**
     * Adds the specified user object to the database. You should ensure that it
     * has an unused id.
     */
    public void addUser(User user) throws SQLException {
        PreparedStatement statement = query("INSERT INTO `user` VALUES (?, ?, ?, ?, ?)");
        statement.setInt(1, user.getId());
        statement.setString(2, user.getFirstName());
        statement.setString(3, user.getSurname());
        statement.setString(4, user.getPassword());
        statement.setBoolean(5, user.isAdmin());
        statement.execute();
    }

    /**
     * Updates the user with the provided {@link User#getId()}.
     */
    public void editUser(User user) throws SQLException {
        PreparedStatement statement
                = query("UPDATE `user` SET first_name = ?, surname = ?, password = ?, is_admin = ? WHERE id = ?");
        statement.setString(1, user.getFirstName());
        statement.setString(2, user.getSurname());
        statement.setString(3, user.getPassword());
        statement.setBoolean(4, user.isAdmin());
        statement.setInt(5, user.getId());
        statement.executeUpdate();
    }

    /**
     * Removes the user with the specified userId.
     */
    public void removeUser(int userId) throws SQLException {
        PreparedStatement statement = query("DELETE FROM `user` WHERE id = ?");
        statement.setInt(1, userId);
        statement.executeUpdate();
    }

    public boolean userExists(String firstName, String surname) throws SQLException {
        PreparedStatement statement
                = query("SELECT COUNT(id) AS total FROM `user` WHERE first_name = ? AND surname = ?");
        statement.setString(1, firstName);
        statement.setString(2, surname);
        return statement.executeQuery().getInt("total") > 0;
    }

    public boolean userExists(int userId) throws SQLException {
        PreparedStatement statement = query("SELECT COUNT(id) AS total FROM `user` WHERE id = ?");
        statement.setInt(1, userId);
        return statement.executeQuery().getInt("total") > 0;
    }

    public User getUser(int userId) throws SQLException {
        PreparedStatement statement
                = query("SELECT first_name, surname, password, is_admin FROM `user` WHERE id = ? LIMIT 1");

        // Execute query
        statement.setInt(1, userId);
        ResultSet rs = statement.executeQuery();

        // Create corresponding User instance
        String firstName = rs.getString("first_name");
        String surname = rs.getString("surname");
        String password = rs.getString("password");
        boolean isAdmin = rs.getBoolean("is_admin");
        return new User(userId, firstName, surname, password, isAdmin);
    }

    public Collection<User> loadUsers() throws SQLException {
        PreparedStatement statement = query("SELECT * FROM `user`");
        ResultSet rs = statement.executeQuery(); // Execute query

        // Create corresponding User instances
        Collection<User> users = new ArrayList<>();

        while (rs.next()) {
            // Get corresponding info
            int userId = rs.getInt("id");
            String firstName = rs.getString("first_name");
            String surname = rs.getString("surname");
            String password = rs.getString("password");
            boolean isAdmin = rs.getBoolean("is_admin");

            // Create user object and add to list
            User user = new User(userId, firstName, surname, password, isAdmin);
            users.add(user);
        }
        return users;
    }

    public void addMechanic(Mechanic mechanic) throws SQLException {
        PreparedStatement statement = query("INSERT INTO `mechanic` VALUES (?, ?, ?, ?)");
        statement.setInt(1, mechanic.getId());
        statement.setString(2, mechanic.getFirstName());
        statement.setString(3, mechanic.getSurname());
        statement.setDouble(4, mechanic.getHourlyWage());
        statement.execute();
    }

    public void editMechanic(Mechanic mechanic) throws SQLException {
        PreparedStatement statement
        = query("UPDATE `mechanic` SET first_name = ?, surname = ?, hourly_wage = ? WHERE mechanic_id = ?");
        statement.setString(1, mechanic.getFirstName());
        statement.setString(2, mechanic.getSurname());
        statement.setDouble(3, mechanic.getHourlyWage());
        statement.setInt(4, mechanic.getId());
        int k = statement.executeUpdate();

        if (k > 0)
            updateInvoiceForMechanicChange(mechanic.getId());
    }

    public void removeMechanic(int mechanicId) throws SQLException {
        PreparedStatement statement = query("DELETE FROM `mechanic` WHERE mechanic_id = ?");
        statement.setInt(1, mechanicId);
        statement.executeUpdate();
    }

    public List<Mechanic> loadMechanics() throws SQLException {
        PreparedStatement statement = query("SELECT * FROM `mechanic`");

        // Execute query
        ResultSet rs = statement.executeQuery();

        // Create corresponding mechanic instances
        List<Mechanic> mechanics = new ArrayList<>();

        while (rs.next()) {
            // Get corresponding info
            int mechId = rs.getInt("mechanic_id");
            String firstName = rs.getString("first_name");
            String surname = rs.getString("surname");
            double hourlyWage = rs.getDouble("hourly_wage");

            // Create mechanic object and add to list
            Mechanic mechanic = new Mechanic(mechId, firstName, surname, hourlyWage);
            mechanics.add(mechanic);
        }
        return mechanics;
    }

    public Mechanic getMechanic(int mechanicId) throws SQLException {
        PreparedStatement statement = query("SELECT * FROM `mechanic` WHERE mechanic_id = ? LIMIT 1");

        // Execute query
        statement.setInt(1, mechanicId);
        ResultSet rs = statement.executeQuery();

        // Create corresponding Mechanic instance
        String firstName = rs.getString("first_name");
        String surname = rs.getString("surname");
        float hourlyWage = rs.getFloat("hourly_wage");
        return new Mechanic(mechanicId, firstName, surname, hourlyWage);
    }

    public boolean mechanicExists(String firstName, String surname) throws SQLException {
        PreparedStatement statement
        = query("SELECT COUNT(mechanic_id) AS total FROM `mechanic` WHERE first_name = ? AND surname = ?");
        statement.setString(1, firstName);
        statement.setString(2, surname);
        return statement.executeQuery().getInt("total") > 0;
    }

    public boolean mechanicExists(int mechanicId) throws SQLException {
        PreparedStatement statement = query("SELECT COUNT(mechanic_id) AS total FROM `mechanic` WHERE mechanic_id = ?");
        statement.setInt(1, mechanicId);
        return statement.executeQuery().getInt("total") > 0;
    }

    /**
     * @return If the mechanic is assigned to any bookings (past or future).
     */
    public boolean mechanicIsNeeded(int mechanicId) throws SQLException {
        PreparedStatement statement = query("SELECT COUNT(mechanic_id) AS total FROM `booking` WHERE mechanic_id = ?");
        statement.setInt(1, mechanicId);
        return statement.executeQuery().getInt("total") > 0;
    }

    /**
     * Removes a booking, its associated parts and its invoice from the  database.
     */
    public void removeBooking(Booking booking) throws SQLException, ParseException {
        PreparedStatement removeBookingStatement = query("DELETE FROM `booking` WHERE booking_id = ?");
        removeBookingStatement.setInt(1, booking.getId());
        removeBookingStatement.executeUpdate();
        removeInvoice(booking.getInvoice().getId());
        removePartsForBooking(booking.getId());
        SPCBooking spcBooking =getSPCBookingForBooking(booking);
        if(!(spcBooking==null))
        removeSPCBookingForBooking(booking);
    }

    public void removeInvoice(int invoiceId) throws SQLException {
        PreparedStatement removeInvoiceStatement = query("DELETE FROM `invoice` WHERE invoice_id = ?");
        removeInvoiceStatement.setInt(1, invoiceId);
        removeInvoiceStatement.executeUpdate();
    }

    public void removePartsForBooking(int bookingId) throws SQLException {
       PreparedStatement removePartsStatement = query("Delete FROM 'PartsInstalled' WHERE booking_id = ?");
        removePartsStatement.setInt(1, bookingId);
        removePartsStatement.executeUpdate();
    }

    public void removeSPCBookingForBooking(Booking bookingB) throws SQLException, ParseException {
        try {
            SPCBooking booking = getSPCBookingForBooking(bookingB);

            PreparedStatement statement = query("DELETE FROM `SPCBooking` WHERE booking_id = ?");
            statement.setInt(1, bookingB.getId());
            statement.executeUpdate();

            deleteSPCVehicleForBooking(booking);
            deleteSPCPartsForBooking(booking);
        } catch (IllegalArgumentException ignored) {
        }
    }


    /**
     * Adds a booking, its associated parts and its invoice from the database.
     * It will also set the id of these objects to the one generated by the
     * database.
     */
    public void addBooking(Booking booking) throws SQLException {
        // Insert booking
        PreparedStatement statement
                = connection.prepareStatement("INSERT INTO `booking` VALUES (null, ?, ?, ?, ?, ?, ?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS);
        int bookingType = booking instanceof DiagnosisRepairBooking ? 1 : 0;
        statement.setInt(1, bookingType);
        statement.setInt(2, booking.getMechanic().getId());
        statement.setString(3, DateHelper.toString(booking.getBookingStartDate()));
        statement.setString(4, DateHelper.toString(booking.getBookingEndDate()));
        statement.setString(5, DateHelper.toString(booking.getMechanicTimeSpent()));
        statement.setInt(6, booking.getCustomer().getId());
        statement.setString(7, booking.getVehicle().getRegistrationNo());

        // Set vehicle mileage
        if (booking instanceof DiagnosisRepairBooking) {
            DiagnosisRepairBooking b = (DiagnosisRepairBooking)booking;
            statement.setInt(8, b.getVehicleMileage());
        } else {
            statement.setInt(8, -1);
        }
        statement.execute();

        try (ResultSet keys = statement.getGeneratedKeys()) {
            if (keys.next()) {
                booking.setBookingId(keys.getInt(1)); // set the booking id as the one from db auto increment
            }
        }

        // Insert invoice
        addInvoiceForBooking(booking);
    }

    /**
     * Adds the invoice for the given booking to the database, and sets its id to the one generated by the dbms.
     */
    public void addInvoiceForBooking(Booking booking) throws SQLException {
        Invoice invoice = booking.getInvoice();
        PreparedStatement statement = query("INSERT INTO `invoice` VALUES (null, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
        statement.setInt(1, booking.getId());
        statement.setDouble(2, invoice.getBookingBaseCost());
        statement.setDouble(3, invoice.getAmtPaid());
        statement.setBoolean(4, invoice.isSettled()); // XXX may be settled due to warranty
        statement.setBoolean(5, invoice.isVehicleWarrantyActive());
        statement.setDouble(6, 0.0);
        statement.setDouble(7, 0.0);
        statement.setDouble(8, invoice.getBookingTotalCost());
        statement.setDouble(9, invoice.getBookingAmountDue());
        statement.execute();

        try (ResultSet keys = statement.getGeneratedKeys()) {
            if (keys.next()) {
                invoice.setId(keys.getInt(1)); // set its id as the one from db auto increment
            }
        }
    }

    public boolean isPublicHoliday(Calendar date) throws SQLException {
        PreparedStatement isPublicHolidayStatement
        = query("SELECT COUNT(date) AS total FROM `public_holidays` WHERE date(date)=?");
        isPublicHolidayStatement.setString(1, String.format("%04d-%02d-%02d", date.get(Calendar.YEAR),
                                                            date.get(Calendar.MONTH) + 1, date.get(Calendar.DAY_OF_MONTH)));
        return isPublicHolidayStatement.executeQuery().getInt("total") > 0;
    }

    public boolean overlapsWithOtherBooking(Calendar bookingStartDate, Calendar bookingEndDate) throws SQLException {
        return overlapsWithOtherBooking(-1, bookingStartDate, bookingEndDate);
    }

    /**
     * If the booking id is not -1, it is ignored in the overlap check - otherwise it is not considered.
     */
    public boolean overlapsWithOtherBooking(int bookingId, Calendar bookingStartDate, Calendar bookingEndDate)
    throws SQLException {
        String compareId = bookingId == -1 ? "" : " AND booking_id <> ?";
        String startDate = DateHelper.toString(bookingStartDate);
        String endDate = DateHelper.toString(bookingEndDate);

        PreparedStatement overlapsStatement = query("SELECT COUNT(*) AS total, start_date AS sd, end_date AS ed"
                                                    + " FROM `booking` WHERE ((? <= sd AND ed <= ?) OR (? <= sd AND ? > sd) OR (? < ed AND ? > ed))" + compareId);
        overlapsStatement.setString(1, startDate);
        overlapsStatement.setString(2, endDate);
        overlapsStatement.setString(3, startDate);
        overlapsStatement.setString(4, endDate);
        overlapsStatement.setString(5, startDate);
        overlapsStatement.setString(6, endDate);

        if (bookingId != -1)
            overlapsStatement.setInt(7, bookingId);
        return overlapsStatement.executeQuery().getInt("total") > 0;
    }
       public boolean overlapsWithOtherBookingSPC( Calendar bookingStartDate, Calendar bookingEndDate, String regNo) throws SQLException {

        String startDate = DateHelperSPC.toString(bookingStartDate);
        String endDate = DateHelperSPC.toString(bookingEndDate);

        PreparedStatement overlapsStatement = query("SELECT COUNT(*) AS total, start_date AS sd, end_date AS ed"
                                                    + " FROM `booking` WHERE ((? <= sd AND ed <= ?) OR (? <= sd AND ? > sd) OR (? < ed AND ? > ed)) AND vehicle_id = ? " );
        overlapsStatement.setString(1, startDate);
        overlapsStatement.setString(2, endDate);
        overlapsStatement.setString(3, startDate);
        overlapsStatement.setString(4, endDate);
        overlapsStatement.setString(5, startDate);
        overlapsStatement.setString(6, endDate);
        overlapsStatement.setString(7, regNo);

        return overlapsStatement.executeQuery().getInt("total") > 0;
    }
       public boolean overlapsWithOtherSPCBookingSPC( Calendar bookingStartDate, Calendar bookingEndDate, String regNo) throws SQLException {

        String startDate = DateHelperSPC.toString(bookingStartDate);
        String endDate = DateHelperSPC.toString(bookingEndDate);

        PreparedStatement overlapsStatement = query("SELECT COUNT(*) AS total, start_date AS sd, end_date AS ed"
                                                    + " FROM `SPCBooking` WHERE ((? <= sd AND ed <= ?) OR (? <= sd AND ? > sd) OR (? < ed AND ? > ed)) AND booking_id IN (SELECT booking_id FROM booking WHERE vehicle_id =? ) " );
        overlapsStatement.setString(1, startDate);
        overlapsStatement.setString(2, endDate);
        overlapsStatement.setString(3, startDate);
        overlapsStatement.setString(4, endDate);
        overlapsStatement.setString(5, startDate);
        overlapsStatement.setString(6, endDate);
        overlapsStatement.setString(7, regNo);

        return overlapsStatement.executeQuery().getInt("total") > 0;
    }

    public List<Booking> getAllBookingsForCustomer(String customerName, boolean partialMatch)
    throws SQLException, ParseException {
        PreparedStatement getAllBookingsStatement = query("SELECT * FROM booking NATURAL JOIN invoice "
                                                          + "INNER JOIN Customer ON booking.customer_id=Customer.id "
                                                          + "WHERE Customer.full_name LIKE ?");
        getAllBookingsStatement.setString(1, partialMatch ? "%" + customerName + "%" : customerName);
        ResultSet rs = getAllBookingsStatement.executeQuery();
        return bookingsFromResultSet(rs);
    }

    public List<Booking> getAllBookingsForCustomerFirstName(String firstName, boolean partialMatch)
            throws SQLException, ParseException {
        PreparedStatement getAllBookingsStatement = query("SELECT * FROM booking NATURAL JOIN invoice "
                + "INNER JOIN Customer ON booking.customer_id=Customer.id "
                + "WHERE Customer.full_name LIKE ?");
        getAllBookingsStatement.setString(1, partialMatch ? "%" + firstName + "% _%" : firstName + " _%");
        ResultSet rs = getAllBookingsStatement.executeQuery();
        return bookingsFromResultSet(rs);
    }

    public List<Booking> getAllBookingsForCustomerSurname(String surname, boolean partialMatch)
            throws SQLException, ParseException {
        PreparedStatement getAllBookingsStatement = query("SELECT * FROM booking NATURAL JOIN invoice "
                + "INNER JOIN Customer ON booking.customer_id=Customer.id "
                + "WHERE Customer.full_name LIKE ?");
        getAllBookingsStatement.setString(1, partialMatch ? "_% %" + surname + "%" : "_% " + surname);
        ResultSet rs = getAllBookingsStatement.executeQuery();
        return bookingsFromResultSet(rs);
    }

    public List<Booking> getAllBookingsForVehicleTemplate(int value) throws SQLException, ParseException {
        PreparedStatement getAllBookingsStatement = query("SELECT * FROM booking NATURAL JOIN invoice WHERE EXISTS"
                + " (SELECT VehicleTemplateID FROM Vehicle WHERE Vehicle.VehicleTemplateID = ? AND booking.vehicle_id = Vehicle.RegistrationNo)");
        getAllBookingsStatement.setInt(1, value);
        ResultSet rs = getAllBookingsStatement.executeQuery();
        return bookingsFromResultSet(rs);
    }

    public List<Booking> getAllBookingsForBookingDate(String value) throws SQLException, ParseException {
        PreparedStatement getAllBookingsStatement = query("SELECT * FROM booking NATURAL JOIN invoice WHERE start_date LIKE ? OR end_date LIKE ?");
        getAllBookingsStatement.setString(1, value);
        getAllBookingsStatement.setString(2, value);
        ResultSet rs = getAllBookingsStatement.executeQuery();
        return bookingsFromResultSet(rs);
    }

    public List<Booking> getAllBookingsForVehicleMake(String make, boolean partialMatch)
    throws SQLException, ParseException {
        PreparedStatement getAllBookingsStatement = query("SELECT * FROM booking NATURAL JOIN invoice WHERE EXISTS"
                                                          + " (SELECT Make FROM VehicleTemplate INNER JOIN Vehicle ON VehicleTemplate.VehicleTemplateID=Vehicle.VehicleTemplateID WHERE VehicleTemplate.Make "
                                                          + "LIKE ? AND Vehicle.`RegistrationNo`=booking.vehicle_id)");
        getAllBookingsStatement.setString(1, partialMatch ? "%" + make + "%" : make);
        ResultSet rs = getAllBookingsStatement.executeQuery();
        return bookingsFromResultSet(rs);
    }

    public List<Booking> getAllBookingsForVehicleRegNum(String regNum, boolean partialMatch)
    throws SQLException, ParseException {
        String comparisonOperator = partialMatch ? "LIKE" : "=";
        PreparedStatement getAllBookingsStatement = query("SELECT * FROM booking NATURAL JOIN invoice WHERE booking.vehicle_id " + comparisonOperator + " ?");
        getAllBookingsStatement.setString(1, partialMatch ? "%" + regNum + "%" : regNum);
        ResultSet rs = getAllBookingsStatement.executeQuery();
        return bookingsFromResultSet(rs);
    }

    public List<Booking> getAllBookingsForVehicle(String regNum)
    throws SQLException, ParseException {
        PreparedStatement getAllBookingsStatement = query("SELECT * FROM booking NATURAL JOIN invoice WHERE booking.vehicle_id = ? COLLATE NOCASE");
        getAllBookingsStatement.setString(1,  "%" + regNum + "%" );
        ResultSet rs = getAllBookingsStatement.executeQuery();
        return bookingsFromResultSet(rs);
    }

    public List<Booking> getAllBookings() throws SQLException, ParseException {
        PreparedStatement getAllBookingsStatement = query("SELECT * FROM booking NATURAL JOIN invoice");
        ResultSet rs = getAllBookingsStatement.executeQuery();
        return bookingsFromResultSet(rs);
    }

    public Booking getBooking(int id) throws SQLException, ParseException {
        PreparedStatement getBookingStatement = query("SELECT * FROM booking NATURAL JOIN invoice WHERE booking_id = ?");
        getBookingStatement.setInt(1, id);
        ResultSet rs = getBookingStatement.executeQuery();
        return (bookingsFromResultSet(rs).get(0));
    }

    /**
     * @return The collection of future bookings.
     */
    public List<Booking> getAllFutureBookings() throws SQLException, ParseException {
        PreparedStatement getAllBookingsStatement = query("SELECT * FROM booking NATURAL JOIN invoice WHERE end_date > ?");
        getAllBookingsStatement.setString(1, DateHelper.toString(Calendar.getInstance()));
        ResultSet rs = getAllBookingsStatement.executeQuery();
        return bookingsFromResultSet(rs);
    }

    /**
     * @return The collection of past bookings.
     */
    public List<Booking> getAllPastBookings() throws SQLException, ParseException {
        PreparedStatement statement = query("SELECT * FROM booking NATURAL JOIN invoice WHERE end_date < ?");
        statement.setString(1, DateHelper.toString(Calendar.getInstance()));
        ResultSet rs = statement.executeQuery();
        return bookingsFromResultSet(rs);
    }

    /**
     * @return The collection of nearest future bookings per vehicle.
     */
    public List<Booking> getAllNextBookingsPerVehicle() throws SQLException, ParseException {
        PreparedStatement getAllBookingsStatement = query("SELECT * FROM booking NATURAL JOIN invoice WHERE end_date IN (SELECT MIN(end_date) FROM booking WHERE end_date > ? GROUP BY vehicle_id)");
		// XXX Consider changing query to: SELECT * FROM Booking INNER JOIN Vehicle ON Booking.vehicleReg = Vehicle.Registration WHERE  Booking.endDate >? GROUP BY Booking.vehicleReg ORDER BY date LIMIT 1
        getAllBookingsStatement.setString(1, DateHelper.toString(Calendar.getInstance()));
        ResultSet rs = getAllBookingsStatement.executeQuery();
        return bookingsFromResultSet(rs);
    }

    private List<Booking> bookingsFromResultSet(ResultSet rs) throws SQLException, ParseException {
        List<Booking> bookings = new ArrayList<>();

        while (rs.next()) {
            // Get corresponding info
            int bookingId = rs.getInt("booking_id");
            int type = rs.getInt("booking_type");
            int mechanicId = rs.getInt("mechanic_id");
            String vehicleId = rs.getString("vehicle_id");
            int customerId = rs.getInt("customer_id");
            Calendar mechanicTimeSpent = DateHelper.toCalendar(rs.getString("mechanic_hrs_spent"));
            Calendar startDate = DateHelper.toCalendar(rs.getString("start_date"));
            Calendar endDate = DateHelper.toCalendar(rs.getString("end_date"));
            int vehicleMileage = rs.getInt("vehicle_mileage");

            // Create booking object and add to list
            Vehicle vehicle = getVehicle(vehicleId);
            Customer customer = getCustomer(customerId);
            Mechanic mechanic = getMechanic(mechanicId);
            Booking booking;

            if (type == 0) {
                booking = new ScheduledMaintenanceBooking(bookingId, startDate, endDate);
            } else if (type == 1) {
                booking = new DiagnosisRepairBooking(bookingId, startDate, endDate);
            } else {
                throw new SQLException("Invalid booking type (" + type + ")");
            }

            if (booking instanceof DiagnosisRepairBooking && vehicleMileage != -1) {
                DiagnosisRepairBooking b = (DiagnosisRepairBooking)booking;
                b.setVehicleMileage(vehicleMileage);
            }
            booking.setVehicle(vehicle);
            booking.setCustomer(customer);
            booking.setMechanic(mechanic);
            booking.setMechanicTimeSpent(mechanicTimeSpent);
            booking.setInvoice(invoiceFromResultSet(rs));
            bookings.add(booking);
        }
        return bookings;
    }

    private Invoice invoiceFromResultSet(ResultSet rs) throws SQLException {
        int id = rs.getInt("invoice_id");
        double bookingBaseCost = rs.getDouble("booking_base_cost");
        double amtPaid = rs.getDouble("amt_paid");
        boolean settled = rs.getInt("payment_complete") == 1;
        boolean vehicleWarrantyActive = rs.getInt("vehicle_warranty_active") == 1;
        double spcTotalCost = rs.getDouble("spc_total_cost");
        double spcAmtDue = rs.getDouble("spc_amt_due");
        double bookingTotalCost = rs.getDouble("booking_total_cost");
        double bookingAmtDue = rs.getDouble("booking_amt_due");
        return new Invoice(id, amtPaid, bookingBaseCost, bookingTotalCost, bookingAmtDue, spcTotalCost, spcAmtDue,
                settled, vehicleWarrantyActive);
    }

    public void addSPC(SPC spc) throws SQLException {
        PreparedStatement addSPCStatement = query("INSERT INTO `spc` VALUES (null, ?, ?, ?, ?, ?, ?)");
        //addSPCStatement.setInt(1, spc.getId());
        addSPCStatement.setString(1, spc.getName());
        addSPCStatement.setString(2, spc.getPhone());
        addSPCStatement.setString(3, spc.getEmail());
        addSPCStatement.setString(4, spc.getCity());
        addSPCStatement.setString(5, spc.getAddress());
        addSPCStatement.setString(6, spc.getPostcode());
        addSPCStatement.execute();
        try (ResultSet keys = addSPCStatement.getGeneratedKeys()) {
            if (keys.next()) {
                spc.setId(keys.getInt(1)); // set the id as the one we got from auto increment
            }
        }
    }

    public void removeSPC(SPC spc) throws SQLException {
        removeSPC(spc.getId());
    }

    /**
     * Removes the spc with the specified spcId
     */
    public void removeSPC(int spcId) throws SQLException {
        PreparedStatement removeUserStatement = query("DELETE FROM `spc` WHERE id = ?");
        removeUserStatement.setInt(1, spcId);
        removeUserStatement.executeUpdate();
    }

    public void editSPC(SPC spc) throws SQLException {
        PreparedStatement editSPCStatement
        = query("UPDATE `spc` SET name = ?, phone = ?, email = ?, city = ?, street_address = ?, postcode = ? WHERE id = ?");
        editSPCStatement.setString(1, spc.getName());
        editSPCStatement.setString(2, spc.getPhone());
        editSPCStatement.setString(3, spc.getEmail());
        editSPCStatement.setString(4, spc.getCity());
        editSPCStatement.setString(5, spc.getAddress());
        editSPCStatement.setString(6, spc.getPostcode());
        editSPCStatement.setInt(7, spc.getId());
        editSPCStatement.executeUpdate();
    }

    public boolean SPCExists(int spcId) throws SQLException {
        PreparedStatement SPCExistsStatement
        = query("SELECT COUNT(id) AS total FROM `spc` WHERE id = ?");
        SPCExistsStatement.setInt(1, spcId);
        return SPCExistsStatement.executeQuery().getInt("total") > 0;
    }

    public SPC getSPC(int spcId) throws SQLException {
        PreparedStatement getUserStatement
        = query("SELECT name, phone, email, city, street_address, postcode FROM `spc` WHERE id = ?");

        // Execute query
        getUserStatement.setInt(1, spcId);
        ResultSet rs = getUserStatement.executeQuery();

        // Create corresponding SPC instance
        String name = rs.getString("name");
        String phone = rs.getString("phone");
        String email = rs.getString("email");
        String streetAddr = rs.getString("street_address");
        String city = rs.getString("city");
        String postcode = rs.getString("postcode");
        return new SPC(spcId, name, phone, email, city, streetAddr, postcode);
    }

    public Collection<SPC> getAllSPCs() throws SQLException {
        PreparedStatement getAllSPCsStatement = query("SELECT * FROM `SPC`");
        // Execute query
        ResultSet rs = getAllSPCsStatement.executeQuery();

        // Create corresponding SPC instances
        Collection<SPC> listOfSPCs = new ArrayList<>();

        while (rs.next()) {
            // Get corresponding info
            int SPCId = rs.getInt("id");
            String name = rs.getString("name");
            String phone = rs.getString("phone");
            String email = rs.getString("email");
            String streetAddr = rs.getString("street_address");
            String city = rs.getString("city");
            String postcode = rs.getString("postcode");

            // Create SPC object and add to list
            SPC spc = new SPC(SPCId, name, phone, email, city, streetAddr, postcode);
            listOfSPCs.add(spc);
        }
        return listOfSPCs;
    }


    public Collection<SPC> getAllSPCsForVehicle(String vehicleRegNo) throws SQLException {
        PreparedStatement getAllSPCsStatement = query("SELECT * FROM `SPC` WHERE id IN (SELECT SPC_id FROM SPCVehicle WHERE spc_booking_id IN(SELECT spc_booking_id from SPCBooking where booking_id IN (SELECT booking_id FROM booking WHERE vehicle_id = ?)) UNION SELECT SPC_id FROM SPCParts WHERE spc_booking_id IN (SELECT spc_booking_id FROM SPCBooking WHERE booking_id IN (SELECT booking_id FROM booking WHERE vehicle_id = ?)))");
        // Execute query
        getAllSPCsStatement.setString(1, vehicleRegNo);
        getAllSPCsStatement.setString(2, vehicleRegNo);
        ResultSet rs = getAllSPCsStatement.executeQuery();

        // Create corresponding SPC instances
        Collection<SPC> listOfSPCs = new ArrayList<>();

        while (rs.next()) {
            // Get corresponding info
            int SPCId = rs.getInt("id");
            String name = rs.getString("name");
            String phone = rs.getString("phone");
            String email = rs.getString("email");
            String streetAddr = rs.getString("street_address");
            String city = rs.getString("city");
            String postcode = rs.getString("postcode");

            // Create SPC object and add to list
            SPC spc = new SPC(SPCId, name, phone, email, city, streetAddr, postcode);
            listOfSPCs.add(spc);
        }
        return listOfSPCs;
    }

    public void addSPCPart(SPCPart part) throws SQLException {
        PreparedStatement addSPCStatement = query("INSERT INTO `SPCParts` VALUES (null, ?, ?, ?, ?, ?, ?, ?, ?)");
        addSPCStatement.setInt(1, part.getSPC().getId());
        addSPCStatement.setInt(2, part.getSPCBooking().getSpcBookingId());
        addSPCStatement.setInt(3, part.getPart().getID());
        addSPCStatement.setString(4, part.getFaultDescription());
        addSPCStatement.setDouble(5, part.getRepairCost());
        addSPCStatement.setString(6, DateHelperSPC.toString(part.getDeliveryDate()));
        addSPCStatement.setString(7, DateHelperSPC.toString(part.getReturnDate()));
        addSPCStatement.setInt(8, part.getInstalledPartId());
        addSPCStatement.execute();
        try (ResultSet keys = addSPCStatement.getGeneratedKeys()) {
            if (keys.next()) {
                part.setID(keys.getInt(1)); // set the id as the one we got from auto increment
            }
        }

    }

    public void deleteSPCPart(SPCPart part) throws SQLException {
        PreparedStatement addSPCStatement = query("DELETE FROM `SPCParts` WHERE id = ?");
        addSPCStatement.setInt(1, part.getId());
        addSPCStatement.execute();
        part.getSPCBooking().setTotalCost(part.getSPCBooking().getSPCBookingTotal()-part.getRepairCost() );
        updateSPCBooking(part.getSPCBooking());


    }
    public void deleteSPCPartsForBooking(SPCBooking booking) throws SQLException {
        PreparedStatement addSPCStatement = query("DELETE FROM `SPCParts` WHERE spc_booking_id = ?");
        addSPCStatement.setInt(1, booking.getSpcBookingId());
        addSPCStatement.execute();

    }

    public void editSPCPart(SPCPart part) throws SQLException {
        PreparedStatement addSPCStatement = query("UPDATE `SPCParts` SET fault_description = ?, repairCost=?, deliveryDate =?, returnDate =? WHERE id = ?");
        addSPCStatement.setString(1, part.getFaultDescription());
        addSPCStatement.setDouble(2, part.getRepairCost());
        addSPCStatement.setString(3, DateHelperSPC.toString(part.getDeliveryDate()));
        addSPCStatement.setString(4, DateHelperSPC.toString(part.getReturnDate()));
        addSPCStatement.setInt(5, part.getId());
        addSPCStatement.execute();


    }
    public Collection<SPCPart> getAllPartsForVehicleAtSPC(String vehicleRegNo, int spcId) throws SQLException, ParseException {
        PreparedStatement getAllSPCsStatement = query("SELECT * FROM `SPCParts` WHERE SPC_id = ? AND spc_booking_id IN (SELECT spc_booking_id FROM SPCbooking WHERE booking_id IN (SELECT booking_id FROM booking WHERE vehicle_id =?))");
        // Execute query
        getAllSPCsStatement.setInt(1,spcId);
        getAllSPCsStatement.setString(2, vehicleRegNo);
        ResultSet rs = getAllSPCsStatement.executeQuery();

        // Create corresponding SPC instances
        Collection<SPCPart> listOfParts = new ArrayList<>();

        while (rs.next()) {
            // Get corresponding info
            int spcPartId = rs.getInt("id");
            int spc_bookingId = rs.getInt("spc_booking_id");
            int partId = rs.getInt("part_id");
            String faultDescription = rs.getString("fault_description");
            Double repairCost = rs.getDouble("repairCost");
            String deliveryDate = rs.getString("deliveryDate");
            String returnDate = rs.getString("returnDate");

            int installedId =rs.getInt("installed_part_id");
            

            // Create SPC object and add to list
            SPCPart part = new SPCPart(spcPartId, (Parts)getStockPartForId(partId), repairCost, DateHelperSPC.toCalendar(deliveryDate), DateHelperSPC.toCalendar(returnDate), getSPC(spcId), getSPCBooking(spc_bookingId), faultDescription, installedId);
            System.out.println("Part added" + part.getPart().getName());
            listOfParts.add(part);
        }
        return listOfParts;
    }

    public void addSPCVehicle(SPCVehicle vehicle) throws SQLException {
        PreparedStatement addSPCStatement = query("INSERT INTO `SPCVehicle` VALUES (null, ?, ?, ?, ?, ?)");
        addSPCStatement.setInt(1, vehicle.getSPC().getId());
        addSPCStatement.setInt(2, vehicle.getSPCBooking().getSpcBookingId());
        addSPCStatement.setString(3, DateHelperSPC.toString(vehicle.getExpDeliveryDate()));
        addSPCStatement.setString(4, DateHelperSPC.toString(vehicle.getExpReturnDate()));
        addSPCStatement.setDouble(5, vehicle.getRepairCost());
        addSPCStatement.execute();

    }
    public void deleteSPCVehicle(SPCVehicle vehicle) throws SQLException {
        PreparedStatement addSPCStatement = query("DELETE FROM `SPCVehicle` WHERE id = ?");
        addSPCStatement.setInt(1, vehicle.getId());
        addSPCStatement.execute();
        
    }

    public void deleteSPCVehicleForBooking(SPCBooking booking) throws SQLException {
        PreparedStatement addSPCStatement = query("DELETE FROM `SPCVehicle` WHERE spc_booking_id = ?");
        addSPCStatement.setInt(1, booking.getSpcBookingId());
        addSPCStatement.execute();

    }

    public SPCVehicle getSPCVehicle(int vehicleId) throws SQLException, ParseException {
        PreparedStatement getUserStatement
        = query("SELECT * FROM `SPCVehicle` WHERE id = ?");

        // Execute query
        getUserStatement.setInt(1, vehicleId);
        ResultSet rs = getUserStatement.executeQuery();

        // Create corresponding SPC instance
        int bookingId = rs.getInt("spc_booking_id");
        int spcId = rs.getInt("SPC_id");
        Calendar  startDate = DateHelperSPC.toCalendar(rs.getString("deliveryDate"));
        Calendar  endDate = DateHelperSPC.toCalendar(rs.getString("returnDate"));
        Double repairCost =rs.getDouble("repairCost");
        SPCVehicle vehicle = new SPCVehicle(startDate, endDate, repairCost, getSPCBooking(bookingId), getSPC(spcId), vehicleId);
        return vehicle;
    }



    public void addSPCBooking (SPCBooking spcBooking) throws SQLException {
        PreparedStatement addSPCBookingStatement = query("INSERT INTO `SPCBooking` VALUES (null, ?, ?, ?, ?)");
        addSPCBookingStatement.setInt(1,spcBooking.getBooking().getId());
        addSPCBookingStatement.setString(2, DateHelperSPC.toString(spcBooking.getStartDate()));
        addSPCBookingStatement.setString(3, DateHelperSPC.toString(spcBooking.getEndDate()));
        addSPCBookingStatement.setDouble(4, spcBooking.getSPCBookingTotal());
        addSPCBookingStatement.execute();
        try (ResultSet keys = addSPCBookingStatement.getGeneratedKeys()) {
            if (keys.next()) {
                spcBooking.setSpcBookingId(keys.getInt(1)); // set the id as the one we got from auto increment
            }
        }

    }

    public void updateSPCBooking (SPCBooking spcBooking) throws SQLException {
        PreparedStatement addSPCBookingStatement = query("UPDATE `SPCBooking` SET start_date=?, end_date = ?, total_cost =? WHERE spc_booking_id =?");
        addSPCBookingStatement.setString(1, DateHelperSPC.toString(spcBooking.getStartDate()));
        addSPCBookingStatement.setString(2, DateHelperSPC.toString(spcBooking.getEndDate()));
        addSPCBookingStatement.setDouble(3, spcBooking.getSPCBookingTotal());
        addSPCBookingStatement.setInt(4, spcBooking.getSpcBookingId());
        addSPCBookingStatement.execute();

    }

    public void deleteSPCBooking (SPCBooking spcBooking) throws SQLException {
        PreparedStatement deleteSPCBookingStatement = query("DELETE FROM `SPCBooking` WHERE spc_booking_id = ?");
        deleteSPCBookingStatement.setInt(1, spcBooking.getSpcBookingId());
        deleteSPCBookingStatement.execute();
        deleteSPCVehicleForBooking(spcBooking);
        deleteSPCPartsForBooking(spcBooking);

    }

    public SPCBooking getSPCBooking(int spcBookingId) throws SQLException, ParseException {
        PreparedStatement getUserStatement
        = query("SELECT * FROM `SPCBooking` WHERE spc_booking_id = ?");

        // Execute query
        getUserStatement.setInt(1, spcBookingId);
        ResultSet rs = getUserStatement.executeQuery();

        // Create corresponding SPC instance
        int bookingId = rs.getInt("booking_id");
        Calendar  startDate = DateHelperSPC.toCalendar(rs.getString("start_date"));
        Calendar  endDate = DateHelperSPC.toCalendar(rs.getString("end_date"));
        Double totalCost =rs.getDouble("total_cost");
        SPCBooking spcBooking = new SPCBooking(spcBookingId, getBooking(bookingId));
        spcBooking.setEndDate(endDate);
        spcBooking.setStartDate(startDate);
        spcBooking.setSPCBookingTotal(totalCost);
        return spcBooking;
    }

     public SPCBooking getCurrentSPCBookingForVehicle(Vehicle vehicle) throws SQLException, ParseException {
       PreparedStatement getAllSPCBookingsStatement = query("SELECT * FROM `SPCBooking` WHERE booking_id IN (SELECT booking_id FROM booking WHERE vehicle_id = ?) AND strftime(end_date)>date('now') AND strftime(start_date)<date('now')");
        getAllSPCBookingsStatement.setString(1, vehicle.getRegistrationNo());
        ResultSet rs = getAllSPCBookingsStatement.executeQuery();
        System.out.print(vehicle.getRegistrationNo());
        SPCBooking spcBooking=null;
        if(rs.next()){
        int spcBookingId = rs.getInt("spc_booking_id");
            int bookingId = rs.getInt("booking_id");
            Calendar  startDate = DateHelperSPC.toCalendar(rs.getString("start_date"));
            Calendar  endDate = DateHelperSPC.toCalendar(rs.getString("end_date"));
            Double totalCost =rs.getDouble("total_cost");
            spcBooking = new SPCBooking(spcBookingId, getBooking(bookingId));
            spcBooking.setEndDate(endDate);
            spcBooking.setStartDate(startDate);
            spcBooking.setSPCBookingTotal(totalCost);
        }
        return spcBooking;
    }

    public SPCBooking getSPCBookingForBooking(Booking booking) throws SQLException, ParseException {
        PreparedStatement statement = query("SELECT * FROM `SPCBooking` WHERE booking_id = ?");

        // Execute query
        statement.setInt(1, booking.getId());
        SPCBooking spcBooking;
        ResultSet rs = statement.executeQuery();
        

        if (!rs.next()) { // no result found
            //throw new IllegalArgumentException(bookingId + " has no associated spc booking.");
            spcBooking=null;
            return spcBooking;
        }

        // Create corresponding SPC instance
        int spcBookingId = rs.getInt("spc_booking_id");
        Calendar startDate = DateHelperSPC.toCalendar(rs.getString("start_date"));
        Calendar endDate = DateHelperSPC.toCalendar(rs.getString("end_date"));
        Double totalCost =rs.getDouble("total_cost");
        spcBooking = new SPCBooking(spcBookingId, booking);
        spcBooking.setEndDate(endDate);
        spcBooking.setStartDate(startDate);
        spcBooking.setSPCBookingTotal(totalCost);
        return spcBooking;
    }



    public Collection<SPCBooking> getAllSPCBookings() throws SQLException, ParseException {
        PreparedStatement getAllSPCBookingsStatement = query("SELECT * FROM `SPCBooking`");
        // Execute query
        ResultSet rs = getAllSPCBookingsStatement.executeQuery();

        // Create corresponding SPC instances
        Collection<SPCBooking> listOfSPCBookings = new ArrayList<>();

        while (rs.next()) {
            // Get corresponding info
            int spcBookingId = rs.getInt("spc_booking_id");
            int bookingId = rs.getInt("booking_id");
            Calendar  startDate = DateHelperSPC.toCalendar(rs.getString("start_date"));
            Calendar  endDate = DateHelperSPC.toCalendar(rs.getString("end_date"));
            Double totalCost =rs.getDouble("total_cost");
            SPCBooking spcBooking = new SPCBooking(spcBookingId, getBooking(bookingId));
            spcBooking.setEndDate(endDate);
            spcBooking.setStartDate(startDate);
            spcBooking.setSPCBookingTotal(totalCost);
            listOfSPCBookings.add(spcBooking);
        }
        return listOfSPCBookings;
    }


    public Collection<SPCBooking> getAllSPCBookingsForVehicle(Vehicle vehicle) throws SQLException, ParseException {
        PreparedStatement getAllSPCBookingsStatement = query("SELECT * FROM `SPCBooking` WHERE booking_id IN (SELECT booking_id FROM booking WHERE vehicle_id = ?)");
        getAllSPCBookingsStatement.setString(1, vehicle.getRegistrationNo());
        // Execute query
        ResultSet rs = getAllSPCBookingsStatement.executeQuery();

        // Create corresponding SPC instances
        Collection<SPCBooking> listOfSPCBookings = new ArrayList<>();

        while (rs.next()) {
            // Get corresponding info
            int spcBookingId = rs.getInt("spc_booking_id");
            int bookingId = rs.getInt("booking_id");
            Calendar  startDate = DateHelperSPC.toCalendar(rs.getString("start_date"));
            Calendar  endDate = DateHelperSPC.toCalendar(rs.getString("end_date"));
            Double totalCost =rs.getDouble("total_cost");
            SPCBooking spcBooking = new SPCBooking(spcBookingId, getBooking(bookingId));
            spcBooking.setEndDate(endDate);
            spcBooking.setStartDate(startDate);
            spcBooking.setSPCBookingTotal(totalCost);
            listOfSPCBookings.add(spcBooking);
        }
        return listOfSPCBookings;
    }

    public Collection<SPCBooking> getAllSPCBookingsForSPCVehicle(SPCVehicle vehicle) throws SQLException, ParseException {
        PreparedStatement getAllSPCBookingsStatement = query("SELECT * FROM `SPCBooking` WHERE spc_booking_id IN (SELECT spc_booking_id FROM SPCVehicle WHERE id = ?)");
        getAllSPCBookingsStatement.setInt(1,vehicle.getId());
        // Execute query
        ResultSet rs = getAllSPCBookingsStatement.executeQuery();

        // Create corresponding SPCBooking instances
        Collection<SPCBooking> listOfSPCBookings = new ArrayList<>();

        while (rs.next()) {
            // Get corresponding info
            int spcBookingId = rs.getInt("spc_booking_id");
            int bookingId = rs.getInt("booking_id");
            Calendar  startDate = DateHelperSPC.toCalendar(rs.getString("start_date"));
            Calendar  endDate = DateHelperSPC.toCalendar(rs.getString("end_date"));
            Double totalCost =rs.getDouble("total_cost");
            SPCBooking spcBooking = new SPCBooking(spcBookingId, getBooking(bookingId));
            spcBooking.setEndDate(endDate);
            spcBooking.setStartDate(startDate);
            spcBooking.setSPCBookingTotal(totalCost);
            listOfSPCBookings.add(spcBooking);
        }
        return listOfSPCBookings;
    }

     public Collection<SPCBooking> searchForSPCBooking(String searchParam) throws SQLException, ParseException {
        PreparedStatement getAllSPCBookingsStatement = query("SELECT * FROM `SPCBooking` WHERE booking_id IN (SELECT booking_id FROM booking WHERE vehicle_id LIKE ?) UNION SELECT * FROM `SPCBooking` WHERE booking_id IN (SELECT booking_id FROM booking WHERE customer_id IN (SELECT id FROM Customer WHERE full_name LIKE ?)) ");
        getAllSPCBookingsStatement.setString(1,"%"+searchParam+"%");
        // Execute query
        ResultSet rs = getAllSPCBookingsStatement.executeQuery();

        // Create corresponding SPCBooking instances
        Collection<SPCBooking> listOfSPCBookings = new ArrayList<>();

        while (rs.next()) {
            // Get corresponding info
            int spcBookingId = rs.getInt("spc_booking_id");
            int bookingId = rs.getInt("booking_id");
            Calendar  startDate = DateHelperSPC.toCalendar(rs.getString("start_date"));
            Calendar  endDate = DateHelperSPC.toCalendar(rs.getString("end_date"));
            Double totalCost =rs.getDouble("total_cost");
            SPCBooking spcBooking = new SPCBooking(spcBookingId, getBooking(bookingId));
            spcBooking.setEndDate(endDate);
            spcBooking.setStartDate(startDate);
            spcBooking.setSPCBookingTotal(totalCost);
            listOfSPCBookings.add(spcBooking);
        }
        return listOfSPCBookings;
    }


    public Collection<SPCVehicle> getAllOutstandingVehiclesAtSPCs() throws SQLException, ParseException {
        PreparedStatement getAllVehiclesStatement = query("SELECT * FROM SPCVehicle WHERE strftime(returnDate)> date('now')");
        // Execute query
        ResultSet rs = getAllVehiclesStatement.executeQuery();

        // Create corresponding SPC instances
        Collection<SPCVehicle> listOfVehicles = new ArrayList<>();

        while (rs.next()) {
            // Get corresponding info
            int id = rs.getInt("id");
            int spcId = rs.getInt("SPC_id");
            Calendar  startDate = DateHelperSPC.toCalendar(rs.getString("deliveryDate"));
            Calendar  endDate = DateHelperSPC.toCalendar(rs.getString("returnDate"));
            Double repairCost =rs.getDouble("repairCost");
            SPCVehicle vehicle = getSPCVehicle(id);
            listOfVehicles.add(vehicle);


        }
        return listOfVehicles;
    }
    
    public Collection<SPCVehicle> getAllReturnedVehiclesAtSPCs() throws SQLException, ParseException {
        PreparedStatement getAllVehiclesStatement = query("SELECT * FROM SPCVehicle WHERE strftime(returnDate)< date('now')");
        // Execute query
        ResultSet rs = getAllVehiclesStatement.executeQuery();

        // Create corresponding SPC instances
        Collection<SPCVehicle> listOfVehicles = new ArrayList<>();

        while (rs.next()) {
            // Get corresponding info
            int id = rs.getInt("id");
            int spcId = rs.getInt("SPC_id");
            Calendar  startDate = DateHelperSPC.toCalendar(rs.getString("deliveryDate"));
            Calendar  endDate = DateHelperSPC.toCalendar(rs.getString("returnDate"));
            Double repairCost =rs.getDouble("repairCost");
            SPCVehicle vehicle = getSPCVehicle(id);
            listOfVehicles.add(vehicle);


        }
        return listOfVehicles;
    }



    public Collection<Vehicle> getAllVehiclesSentToSPC(SPC spc, String registration) throws SQLException, ParseException {
        PreparedStatement getAllVehiclesStatement = query("SELECT * FROM Vehicle WHERE RegistrationNo IN(SELECT vehicle_id FROM booking WHERE booking_id IN (SELECT booking_id FROM SPCBooking WHERE spc_booking_id IN (SELECT spc_booking_id FROM SPCVehicle WHERE SPC_id = ? )))AND( RegistrationNo LIKE ? OR customer_id IN (SELECT id FROM CUSTOMER WHERE full_name LIKE ? ))");
        getAllVehiclesStatement.setInt(1, spc.getId());
        getAllVehiclesStatement.setString(2, "%"+registration+"%");
        getAllVehiclesStatement.setString(3, "%"+registration+"%");

        // Execute query
        ResultSet rs = getAllVehiclesStatement.executeQuery();

        // Create corresponding SPC instances
        Collection<Vehicle> listOfVehicles = new ArrayList<>();

        while (rs.next()) {
            // Get corresponding info
            String RegNo = rs.getString("RegistrationNo");
            Vehicle vehicle = getVehicle(RegNo);
            listOfVehicles.add(vehicle);
        }


        return listOfVehicles;
    }


       public Collection<SPCPart> getAllPartsForSPCBooking(SPCBooking booking) throws SQLException, ParseException {
        PreparedStatement getAllPartsStatement = query("SELECT * FROM SPCParts WHERE spc_booking_id = ?");
        getAllPartsStatement.setInt(1, booking.getSpcBookingId());
        // Execute query
        ResultSet rs = getAllPartsStatement.executeQuery();

        Collection<SPCPart> listOfParts = new ArrayList<>();

        while (rs.next()) {
            // Get corresponding info
            int spcPartId = rs.getInt("id");
            int spcId = rs.getInt("SPC_id");
            int partId = rs.getInt("part_id");
            int spc_bookingId = rs.getInt("spc_booking_id");
            String description = rs.getString("fault_description");
            Double repairCost = rs.getDouble("repairCost");
            String deliveryDate = rs.getString("deliveryDate");
            String returnDate = rs.getString("returnDate");
            // Create SPCPart object and add to list
            SPCPart part = new SPCPart(spcPartId,(Parts)getStockPartForId(partId), repairCost, DateHelperSPC.toCalendar(deliveryDate), DateHelperSPC.toCalendar(returnDate), getSPC(spcId), getSPCBooking(spc_bookingId), description, 0);
            listOfParts.add(part);
        }
        return listOfParts;
    }


    public SPCVehicle getSPCVehicleForSPCBooking(SPCBooking booking) throws SQLException, ParseException {
        PreparedStatement getUserStatement
        = query("SELECT * FROM `SPCVehicle` WHERE spc_booking_id = ?");

        // Execute query
        getUserStatement.setInt(1, booking.getSpcBookingId());
        ResultSet rs = getUserStatement.executeQuery();

        // Create corresponding SPC instance
        int bookingId = rs.getInt("spc_booking_id");
        int spcId = rs.getInt("SPC_id");
        Calendar  startDate = DateHelperSPC.toCalendar(rs.getString("deliveryDate"));
        Calendar  endDate = DateHelperSPC.toCalendar(rs.getString("returnDate"));
        Double repairCost =rs.getDouble("repairCost");
        int vehicleId = rs.getInt("id");
        SPCVehicle vehicle = new SPCVehicle(startDate, endDate, repairCost, getSPCBooking(bookingId), getSPC(spcId), vehicleId);
        return vehicle;
    }



    public Collection<SPCPart> getAllOutstandingPartsAtSPCs() throws SQLException, ParseException {
        PreparedStatement getAllVehiclesStatement = query("SELECT * FROM SPCParts WHERE strftime(returnDate)>date('now')");
        // Execute query
        ResultSet rs = getAllVehiclesStatement.executeQuery();

        Collection<SPCPart> listOfParts = new ArrayList<>();

        while (rs.next()) {
            // Get corresponding info
            int spcPartId = rs.getInt("id");
            int spcId = rs.getInt("SPC_id");
            int spc_bookingId = rs.getInt("spc_booking_id");
            int partId = rs.getInt("part_id");
            String description = rs.getString("fault_description");
            Double repairCost = rs.getDouble("repairCost");
            String deliveryDate = rs.getString("deliveryDate");
            String returnDate = rs.getString("returnDate");


            // Create SPCPart object and add to list
            SPCPart part = new SPCPart(spcPartId,(Parts)getStockPartForId(partId), repairCost, DateHelperSPC.toCalendar(deliveryDate), DateHelperSPC.toCalendar(returnDate), getSPC(spcId), getSPCBooking(spc_bookingId), description, 0);
            listOfParts.add(part);
        }
        return listOfParts;
    }
    
     public Collection<SPCPart> getAllReturnedPartsAtSPCs() throws SQLException, ParseException {
        PreparedStatement getAllVehiclesStatement = query("SELECT * FROM SPCParts WHERE strftime(returnDate)<date('now')");
        // Execute query
        ResultSet rs = getAllVehiclesStatement.executeQuery();

        Collection<SPCPart> listOfParts = new ArrayList<>();

        while (rs.next()) {
            // Get corresponding info
            int spcPartId = rs.getInt("id");
            int spcId = rs.getInt("SPC_id");
            int spc_bookingId = rs.getInt("spc_booking_id");
            int partId = rs.getInt("part_id");
            String description = rs.getString("fault_description");
            Double repairCost = rs.getDouble("repairCost");
            String deliveryDate = rs.getString("deliveryDate");
            String returnDate = rs.getString("returnDate");


            // Create SPCPart object and add to list
            SPCPart part = new SPCPart(spcPartId,(Parts)getStockPartForId(partId), repairCost, DateHelperSPC.toCalendar(deliveryDate), DateHelperSPC.toCalendar(returnDate), getSPC(spcId), getSPCBooking(spc_bookingId), description, 0);
            listOfParts.add(part);
        }
        return listOfParts;
    }

    public Collection<SPCPart> getAllPartsSentToSPC(SPC spc) throws SQLException, ParseException {
        PreparedStatement getAllPartsStatement = query("SELECT * FROM SPCParts WHERE SPC_id = ?");
        getAllPartsStatement.setInt(1, spc.getId());
        // Execute query
        ResultSet rs = getAllPartsStatement.executeQuery();

        Collection<SPCPart> listOfParts = new ArrayList<>();

        while (rs.next()) {
            // Get corresponding info
            int spcPartId = rs.getInt("id");
            int spcId = rs.getInt("SPC_id");
            int partId = rs.getInt("part_id");
            int spc_bookingId = rs.getInt("spc_booking_id");
            String description = rs.getString("fault_description");
            Double repairCost = rs.getDouble("repairCost");
            String deliveryDate = rs.getString("deliveryDate");
            String returnDate = rs.getString("returnDate");
            // Create SPCPart object and add to list
            SPCPart part = new SPCPart(spcPartId,(Parts)getStockPartForId(partId), repairCost, DateHelperSPC.toCalendar(deliveryDate), DateHelperSPC.toCalendar(returnDate), getSPC(spcId), getSPCBooking(spc_bookingId), description,0);
            listOfParts.add(part);
        }
        return listOfParts;
    }




    public void addCustomer(Customer customer) throws SQLException {
        PreparedStatement addCustomerStatement = query("INSERT INTO `Customer` VALUES (?,?, ?, ?, ?, ?,?,?)");
        addCustomerStatement.setInt(1, customer.getId());
        addCustomerStatement.setString(2, customer.getFullName());
        addCustomerStatement.setString(3, customer.getPhone());
        addCustomerStatement.setString(4, customer.getEmail());
        addCustomerStatement.setString(5, customer.getCity());
        addCustomerStatement.setString(6, customer.getStreetAddress());
        addCustomerStatement.setString(7, customer.getPostcode());
        addCustomerStatement.setString(8, customer.type.name());
        addCustomerStatement.execute();
    }

    public void removeCustomer(Customer customer) throws SQLException {
        removeCustomer(customer.getId());
        
        removeBooking(customer.getId());
        removeVehicle(customer.getId());
        
    }

    /**
     * Removes the customer with the specified CAId
     */
    public void removeCustomer(int customerId) throws SQLException {
        PreparedStatement removeUserStatement = query("DELETE FROM `Customer` WHERE id = ?");
        removeUserStatement.setInt(1, customerId);
        removeUserStatement.executeUpdate();
    }
    
    public void removeBooking(int customerId) throws SQLException {
        PreparedStatement removeUserStatement = query("DELETE FROM `booking` WHERE customer_id = ?");
        removeUserStatement.setInt(1, customerId);
        removeUserStatement.executeUpdate();
    }
    
    public void removeVehicle(int customerId) throws SQLException {
        PreparedStatement removeUserStatement = query("DELETE FROM `Vehicle` WHERE customer_id = ?");
        removeUserStatement.setInt(1, customerId);
        removeUserStatement.executeUpdate();
    }

    public void editCustomer(Customer customer) throws SQLException {
        PreparedStatement editCustomerStatement = query("UPDATE `Customer` SET full_name = ?, phone = ?, email_address = ?, city = ?, street_address = ?, postcode = ?, type = ? WHERE id = ?");

        editCustomerStatement.setString(1, customer.getFullName());
        editCustomerStatement.setString(2, customer.getPhone());
        editCustomerStatement.setString(3, customer.getEmail());
        editCustomerStatement.setString(4, customer.getCity());
        editCustomerStatement.setString(5, customer.getStreetAddress());
        editCustomerStatement.setString(6, customer.getPostcode());
        editCustomerStatement.setString(7, customer.type.name());
        editCustomerStatement.setInt(8, customer.getId());
        editCustomerStatement.execute();
    }

    public boolean customerExists(String fullName, String email) throws SQLException {
        PreparedStatement custExistsStatement
        = query("SELECT COUNT(id) AS total FROM `Customer` WHERE full_name = ? AND email_address = ?");
        custExistsStatement.setString(1, fullName);
        custExistsStatement.setString(2, email);
        return custExistsStatement.executeQuery().getInt("total") > 0;
    }

    public boolean customerExists(int customerId) throws SQLException {
        PreparedStatement mechExistsStatement
        = query("SELECT COUNT(id) AS total FROM `Customer` WHERE id = ?");
        mechExistsStatement.setInt(1, customerId);
        return mechExistsStatement.executeQuery().getInt("total") > 0;
    }

    public Customer getCustomer(String customerName) throws SQLException {
        PreparedStatement getCustomerStatement = query("SELECT * FROM `Customer` WHERE full_name=? COLLATE NOCASE");
        getCustomerStatement.setString(1, customerName);
        ResultSet rs = getCustomerStatement.executeQuery();

        int id = rs.getInt("id");
        String phone = rs.getString("phone");
        String email = rs.getString("email_address");
        String city = rs.getString("city");
        String streetAddr = rs.getString("street_address");
        String postcode = rs.getString("postcode");
        CustomerType type;
        if(rs.getString("type").equals("Private")){
            type = CustomerType.Private;
        }
        else{
             type = CustomerType.Business;
        }
        return new Customer(id, customerName, phone, email, city,streetAddr, postcode, type);
    }

    public Customer getCustomer(int customerId) throws SQLException {
        PreparedStatement getCustomerStatement
        = query("SELECT full_name, phone, email_address,city, street_address, postcode, type FROM `Customer` WHERE id = ?");

        getCustomerStatement.setInt(1, customerId);
        ResultSet rs = getCustomerStatement.executeQuery();

        String name = rs.getString("full_name");
        String phone = rs.getString("phone");
        String email = rs.getString("email_address");
        String city = rs.getString("city");
        String streetAddr = rs.getString("street_address");
        String postcode = rs.getString("postcode");
        CustomerType type;
       if(rs.getString("type").equals("Private")){
            type = CustomerType.Private;
        }
        else{
             type = CustomerType.Business;
        }

        return new Customer(customerId, name, phone, email, city,streetAddr, postcode, type);
    }
        public ArrayList<Vehicle> getVehicleReg() throws SQLException, ParseException {
        PreparedStatement getAllCustomersStatement = query("SELECT RegistrationNo FROM Vehicle ");

        ArrayList<Vehicle> vehicles = new ArrayList<>();
        ResultSet rs = getAllCustomersStatement.executeQuery();
        while (rs.next()) {

         String RegNo = rs.getString("RegistrationNo");
         Vehicle v = getVehicle(RegNo);
           vehicles.add(v);

        }
        return vehicles;
    }

    public Collection<Customer> getCustomersByVehicleReg(String regno) throws SQLException {
        PreparedStatement getAllCustomersWithReg = query("SELECT Customer.full_name, Customer.phone, "
                + "Customer.email_address, Customer.city,Customer.street_address, "
                + "Customer.postcode, Customer.type,Customer.id, "
                + "Vehicle.RegistrationNo FROM `Customer` "
                + "JOIN Vehicle ON Customer.id = Vehicle.customer_id "
                + "WHERE Vehicle.RegistrationNo LIKE ?  COLLATE NOCASE ");
        getAllCustomersWithReg.setString(1,regno);

         ResultSet rs = getAllCustomersWithReg.executeQuery();

        // Create corresponding customer instances
        Collection<Customer> listOfCustomersWithReg = new ArrayList<>();

        while (rs.next()) {
            // Get corresponding info
            int CustomerId = rs.getInt("id");
            String name = rs.getString("full_name");
            String phone = rs.getString("phone");
            String email = rs.getString("email_address");
            String city = rs.getString("city");
            String streetAddr = rs.getString("street_address");
            String postcode = rs.getString("postcode");
             CustomerType type;
       if(rs.getString("type").equals("Private")){
            type = CustomerType.Private;
        }
        else{
             type = CustomerType.Business;
        }

            // Create customer object and add to list
            Customer customer = new Customer(CustomerId, name, phone, email, city,streetAddr, postcode, type);
            listOfCustomersWithReg.add(customer);
        }
        return listOfCustomersWithReg;
    }
    public ExtCustomer getCustomer1(int customerId) throws SQLException {
        PreparedStatement getCustomerStatement
        = query("SELECT full_name, phone, email_address, city,street_address, postcode, type FROM `Customer` WHERE id = ?");

        getCustomerStatement.setInt(1, customerId);
        ResultSet rs = getCustomerStatement.executeQuery();

        String name = rs.getString("full_name");
        String phone = rs.getString("phone");
        String email = rs.getString("email_address");
                String city = rs.getString("city");

        String streetAddr = rs.getString("street_address");
        String postcode = rs.getString("postcode");
CustomerType type;
  if(rs.getString("type").equals("Private")){
            type = CustomerType.Private;
        }
        else{
             type = CustomerType.Business;
        }
        return new ExtCustomer(customerId, name, phone, email,city, streetAddr, postcode, type);
    }

    public Collection<Customer> getCustomersWithFullName(String FullName) throws SQLException {
        PreparedStatement getAllCustomersWithName = query("SELECT * FROM `Customer` WHERE full_name = ? COLLATE NOCASE");

        // Execute query
        getAllCustomersWithName.setString(1, FullName);
        ResultSet rs = getAllCustomersWithName.executeQuery();

        // Create corresponding customer instances
        Collection<Customer> listOfCustomersWithName = new ArrayList<>();

        while (rs.next()) {
            // Get corresponding info
            int CustomerId = rs.getInt("id");
            String name = rs.getString("full_name");
            String phone = rs.getString("phone");
            String email = rs.getString("email_address");
            String city = rs.getString("city");
            String streetAddr = rs.getString("street_address");
            String postcode = rs.getString("postcode");
             CustomerType type;
       if(rs.getString("type").equals("Private")){
            type = CustomerType.Private;
        }
        else{
             type = CustomerType.Business;
        }

            // Create customer object and add to list
            Customer customer = new Customer(CustomerId, name, phone, email, city,streetAddr, postcode, type);
            listOfCustomersWithName.add(customer);
        }
        return listOfCustomersWithName;
    }

    public Collection<Customer> getCustomersWithFirstLastName(String FirstLastName) throws SQLException {
        PreparedStatement getAllCustomersWithName = query("SELECT * FROM `Customer` WHERE full_name LIKE ? COLLATE NOCASE");

        // Execute query
        getAllCustomersWithName.setString(1, FirstLastName);
        ResultSet rs = getAllCustomersWithName.executeQuery();

        // Create corresponding customer instances
        Collection<Customer> listOfCustomersWithName = new ArrayList<>();

        while (rs.next()) {
            // Get corresponding info
            int CustomerId = rs.getInt("id");
            String name = rs.getString("full_name");
            String phone = rs.getString("phone");
            String email = rs.getString("email_address");
                    String city = rs.getString("city");

            String streetAddr = rs.getString("street_address");
 CustomerType type;
       if(rs.getString("type").equals("Private")){
            type = CustomerType.Private;
        }
        else{
             type = CustomerType.Business;
        }
        String postcode = rs.getString("postcode");

            // Create customer object and add to list
            Customer customer = new Customer(CustomerId, name, phone, email,city, streetAddr, postcode, type);
            listOfCustomersWithName.add(customer);
        }
        return listOfCustomersWithName;
    }

    public Collection<Customer> getAllCustomers() throws SQLException {
        PreparedStatement getAllCustomersStatement = query("SELECT * FROM `Customer`");

        // Execute query
        ResultSet rs = getAllCustomersStatement.executeQuery();

        // Create corresponding customer instances
        Collection<Customer> listOfCustomers = new ArrayList<>();

        while (rs.next()) {
            // Get corresponding info
            int CustomerId = rs.getInt("id");
            String name = rs.getString("full_name");
            String phone = rs.getString("phone");
            String email = rs.getString("email_address");
                    String city = rs.getString("city");

            String streetAddr = rs.getString("street_address");
 CustomerType type;
        if(rs.getString("type").equals("Private")){
            type = CustomerType.Private;
        }
        else{
             type = CustomerType.Business;
        }
        String postcode = rs.getString("postcode");

            // Create customer object and add to list
            Customer customer = new Customer(CustomerId, name, phone, email,city, streetAddr, postcode, type);
            listOfCustomers.add(customer);
        }
        return listOfCustomers;
    }

     public void addPartTransaction(Transactions tran) throws SQLException {
        PreparedStatement addPartTranStatement = query("INSERT INTO `PartTransactions` VALUES (?, ?, ?, ?)");
        List<String> x = getPartDetails(tran.getName());
        addPartTranStatement.setInt(1, getUniqueTransactionid());
        addPartTranStatement.setInt(2, Integer.parseInt(x.get(0)));
        addPartTranStatement.setInt(3, tran.gettQuantity());
        addPartTranStatement.setBoolean(4, tran.getIncoming());
        addPartTranStatement.execute();
    }

    public void addStockPart(StockParts sp) throws SQLException{ //Reference: Used the addUser method in database.java as an example
        PreparedStatement addPartStatement = query("INSERT INTO `Parts` VALUES (?, ?, ?, ?, ?)");
        addPartStatement.setInt(1, sp.getID());
        addPartStatement.setString(2, sp.getName());
        addPartStatement.setString(3, sp.getDescription());
        addPartStatement.setInt(4, sp.getQuantity());
        addPartStatement.setDouble(5, sp.getCost());
        addPartStatement.execute();
    }

     public void editPartInstalled(InstalledParts pi, int row) throws SQLException {
        PreparedStatement editInstalledPartsStatement = query ("UPDATE PartsInstalled\n" +
            "SET part_id = ?, booking_id = ?, install_date = ?, waranty_expiration_date = ?, quantity_installed = ?\n" +
            "WHERE id = ?");
        editInstalledPartsStatement.setInt(1, pi.getID());
        editInstalledPartsStatement.setInt(2, pi.getBookID());
        editInstalledPartsStatement.setString(3, DateHelper.toString(pi.getInstallDateDatabase()));
        editInstalledPartsStatement.setString(4, DateHelper.toString(pi.getExpiryDateDatabase()));
        editInstalledPartsStatement.setInt(5, pi.getInstallQuantity());
        editInstalledPartsStatement.setInt(6, row);
        int k = editInstalledPartsStatement.executeUpdate();

         if (k > 0)
             updateInvoiceForPartWarrantyChange(pi.getID());
     }
     
     public void editPartWithdrawals(PartWithdrawals pw, int row) throws SQLException {
        PreparedStatement editPartWithdrawalsStatement = query ("UPDATE PartsInstalled\n" +
            "SET part_id = ?, booking_id = ?, install_date = ?, waranty_expiration_date = ?, quantity_installed = ?, type = ?\n" +
            "WHERE id = ?");
        editPartWithdrawalsStatement.setInt(1, pw.getPartID());
        editPartWithdrawalsStatement.setInt(2, pw.getBookingID());
        editPartWithdrawalsStatement.setString(3, DateHelper.toString(pw.getBookDateDatabase()));
        Calendar cal = pw.getBookDateDatabase();
        cal.add(Calendar.YEAR, 1);
        editPartWithdrawalsStatement.setString(4, DateHelper.toString(cal));
        editPartWithdrawalsStatement.setInt(5, pw.getQuantity());
        editPartWithdrawalsStatement.setInt(6, pw.getType());
        editPartWithdrawalsStatement.setInt(7, row);
        editPartWithdrawalsStatement.execute();
     }

     public void editPartDelivery(Transactions t, int row) throws SQLException {
        PreparedStatement editPartDeliveryStatement = query ("UPDATE PartTransactions\n" +
            "SET Part_id = ?, Quantity_change = ? WHERE Transaction_id = ?");
        editPartDeliveryStatement.setInt(1, t.getID());
        editPartDeliveryStatement.setInt(2, t.gettQuantity());
        editPartDeliveryStatement.setInt(3, row);
        editPartDeliveryStatement.execute();
     }


     public void editStockParts(StockParts sp, int id) throws SQLException {
        PreparedStatement editPartsStatement = query ("UPDATE Parts\n" +
            "SET name = ?, description = ?, quantity = ?, cost = ?\n" +
            "WHERE id = ?");
        editPartsStatement.setString(1, sp.getName());
        editPartsStatement.setString(2, sp.getDescription());
        editPartsStatement.setInt(3, sp.getQuantity());
        editPartsStatement.setDouble(4, sp.getCost());
        editPartsStatement.setInt(5, id);
        int k = editPartsStatement.executeUpdate();

        if (k > 0)
            updateInvoiceForStockPartChange(id);
     }

    public void addPartInstalled(InstalledParts pi) throws SQLException {
        PreparedStatement addInstalledPartsStatement = query("INSERT INTO `PartsInstalled` VALUES (?, ?, ?, ?, ?,?,?)");
        addInstalledPartsStatement.setInt(1, getUniquePartInstalledid());
        addInstalledPartsStatement.setInt(2, pi.getID());
        addInstalledPartsStatement.setInt(3, pi.getBookID());
        addInstalledPartsStatement.setString(4, DateHelper.toString(pi.getInstallDateDatabase()));
        addInstalledPartsStatement.setString(5, DateHelper.toString(pi.getExpiryDateDatabase()));
        addInstalledPartsStatement.setInt(6, pi.getInstallQuantity());
        addInstalledPartsStatement.setInt(7, 1);
        int k = addInstalledPartsStatement.executeUpdate();

        if (k > 0)
            updateInvoiceForPartWarrantyChange(pi.getID());
    }
    
        
    public List<Integer> getPartsInstalledQuantityWithID(int id) throws SQLException{
        PreparedStatement getPartsInstalledStatement = query("SELECT [PartsInstalled].quantity_installed, [PartsInstalled].part_id FROM [PartsInstalled] WHERE [PartsInstalled].id = ?");
        getPartsInstalledStatement.setInt(1,id);
        ResultSet rs = getPartsInstalledStatement.executeQuery();
        List<Integer> pDetails = new ArrayList<>();
        pDetails.add(rs.getInt("quantity_installed"));
        pDetails.add(rs.getInt("part_id"));
        return pDetails;
    }
    
    public void UpdateStockQuantity(int quantity, int partid) throws SQLException{
        PreparedStatement updateStockQuantityStatement = query ("UPDATE [Parts] SET quantity = quantity + ? WHERE [Parts].id = ?");
        updateStockQuantityStatement.setInt(1, quantity);
        updateStockQuantityStatement.setInt(2, partid);
        updateStockQuantityStatement.execute();
     }

    
    public void addPartWithdrawal(PartWithdrawals pw) throws SQLException {
        PreparedStatement addInstalledPartsStatement = query("INSERT INTO `PartsInstalled` VALUES (?, ?, ?, ?, ?,?,?)");
        addInstalledPartsStatement.setInt(1, getUniquePartInstalledid());
        addInstalledPartsStatement.setInt(2, pw.getPartID());
        addInstalledPartsStatement.setInt(3, pw.getBookingID());
        addInstalledPartsStatement.setString(4, DateHelper.toString(pw.getBookDateDatabase()));
        Calendar cal = pw.getBookDateDatabase();
        cal.add(Calendar.YEAR, 1);
        addInstalledPartsStatement.setString(5, DateHelper.toString(cal));
        addInstalledPartsStatement.setInt(6, pw.getQuantity());
        addInstalledPartsStatement.setInt(7, pw.getType());
        addInstalledPartsStatement.execute();
    }

    
    public Double getPartCostWithPartID(int id) throws SQLException{
        PreparedStatement getPartCostStatement = query("SELECT [Parts].cost FROM `Parts` WHERE [Parts].ID = ?");
        getPartCostStatement.setInt(1, id);
        ResultSet rs = getPartCostStatement.executeQuery();
        Double cost = rs.getDouble("cost");
        return cost;
    }
    public int getUniquePartInstalledid() throws SQLException
    {
        PreparedStatement getMaxID = query("SELECT * \n" +
                                           "    FROM    'PartsInstalled'\n" +
                                           "    WHERE   id = (SELECT MAX(id)  FROM 'PartsInstalled');");
        ResultSet rs = getMaxID.executeQuery();
        int ID = rs.getInt("id");
        ID++;
        return ID;
    }
    
    public boolean checkPastBookingForPartsInstalled(int id) throws SQLException, ParseException {
        boolean check = true;
        PreparedStatement getEndDateStatement = query("SELECT\n" +
        "[booking].end_date AS bEnd\n" +
        "FROM [booking]\n" +
        "JOIN [Customer] ON [booking].customer_id = [Customer].id\n" +
        "JOIN ([PartsInstalled] inner join [Parts] ON [PartsInstalled].part_id = [Parts].id)[PartsInstalled] ON [booking].booking_id = [PartsInstalled].booking_id \n" +
        "WHERE [PartsInstalled].type = 1 AND [PartsInstalled].id = ?");
        getEndDateStatement.setInt(1, id);
        ResultSet rs = getEndDateStatement.executeQuery();
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        java.util.Date bookDate = format.parse(rs.getString("bEnd"));
            Calendar EndDate = Calendar.getInstance();
            Calendar today = Calendar.getInstance();
            today.getTime();
            EndDate.setTime(bookDate);
            check = EndDate.after(today);
        return check;
    }

    public int getUniqueTransactionid() throws SQLException
    {
        PreparedStatement getMaxID = query("SELECT * \n" +
                                           "    FROM    'PartTransactions'\n" +
                                           "    WHERE   Transaction_id = (SELECT MAX(Transaction_id)  FROM 'PartTransactions');");
        ResultSet rs = getMaxID.executeQuery();
        int ID = rs.getInt("Transaction_id");
        ID++;
        return ID;
    }


    public List<StockParts> getAllStockParts() throws SQLException {
        PreparedStatement getAllStockPartsStatement = query("SELECT * FROM `Parts`");

        // Execute query
        ResultSet rs = getAllStockPartsStatement.executeQuery();

        // Create corresponding part instances
        List<StockParts> listOfStockParts = new ArrayList<>();

        while (rs.next()) {
            // Get corresponding info
            int ID = rs.getInt("ID");
            String Name = rs.getString("Name");
            String Description = rs.getString("Description");
            int Quantity = rs.getInt("Quantity");
            double Cost = rs.getDouble("Cost");

            // Create part object and add to list
            StockParts StockParts = new StockParts(ID, Name, Description, Quantity, Cost);
            listOfStockParts.add(StockParts);
        }
        return listOfStockParts;
    }

    public Collection<Vehicle> getAllVehiclesForPartsInstalled(String Reg) throws SQLException, ParseException
    {
        PreparedStatement getVehicleFromRegStatement = query("SELECT * FROM `Vehicle` WHERE RegistrationNo= ? ");
        getVehicleFromRegStatement.setString(1, Reg);
        ResultSet rs = getVehicleFromRegStatement.executeQuery();

        // Create corresponding SPC instances
        Collection<Vehicle> listOfVehicles = new ArrayList<>();

        while (rs.next()) {
            // Get corresponding info
            String RegNo = rs.getString("RegistrationNo");
            int VehicleTemplateID = rs.getInt("VehicleTemplateId");
            VehicleTemplate template = Database.getInstance().getTemplate(VehicleTemplateID);
            String Colour = rs.getString("Colour");
            DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
            java.util.Date MotDate = df.parse(rs.getString("MOTRenewalDate"));
            Calendar MOTRenewalDate = Calendar.getInstance();
            MOTRenewalDate.setTime(MotDate);
            boolean Warranty = rs.getInt("Warranty") == 1;
            java.util.Date LastServDate = df.parse(rs.getString("LastServiceDate"));
            Calendar LastServiceDate = Calendar.getInstance();
            LastServiceDate.setTime(LastServDate);
            double CurrentMileage = rs.getDouble("CurrentMileage");
            int Customerid = rs.getInt("customer_id");
            Customer CustomerID = Database.getInstance().getCustomer(Customerid);
            int WarrantyID = rs.getInt("WarrantyID");
            Warranty warranty = Database.getInstance().getWarrantyforVehicle(WarrantyID);
            listOfVehicles.add(new Vehicle(RegNo, template, Colour, MOTRenewalDate, Warranty,LastServiceDate, CurrentMileage,CustomerID,warranty));
        }
        return listOfVehicles;
    }
    
    public double getTotalCostWithoutWarranty(int bookingId) throws SQLException{
        PreparedStatement getReplacementPartCosts = query("SELECT [Parts].cost AS partCost\n" +
        "FROM [PartsInstalled]\n" +
        "JOIN [Parts] ON [PartsInstalled].part_id = [Parts].id\n" +
        "WHERE [PartsInstalled].type = 1 AND [PartsInstalled].booking_id = ?;");
        getReplacementPartCosts.setInt(1, bookingId);
        double totalcost = 0;
        double replacementTotalcost = 0;
        ResultSet rs = getReplacementPartCosts.executeQuery();
         while (rs.next()) {
             replacementTotalcost = replacementTotalcost + rs.getDouble("partCost");
         }
        totalcost = getRepairTotalCostWithoutWarranty(bookingId) + replacementTotalcost;
        
        return totalcost;
    }
    
    public double getRepairTotalCostWithoutWarranty(int id) throws SQLException{
        PreparedStatement getReplacementPartCosts = query("SELECT [Parts].cost AS partCost\n" +
        "FROM [PartsInstalled]\n" +
        "JOIN [Parts] ON [PartsInstalled].part_id = [Parts].id\n" +
        "WHERE [PartsInstalled].type = 0 AND [PartsInstalled].booking_id = ?;");
        getReplacementPartCosts.setInt(1, id);
        double totalcost = 0;
        ResultSet rs = getReplacementPartCosts.executeQuery();
         while (rs.next()) {
             totalcost = totalcost + rs.getDouble("partCost");
         }
         totalcost = totalcost * 0.2;
        return totalcost;
    }
    
    public double getTotalCostWithWarranty(int id) throws SQLException{
        double totalcost = getTotalCostWithoutWarranty(id);     
        totalcost = totalcost - getTotalWarrantyPartCost(id);
        
        return totalcost;
    }
    
    public double getTotalWarrantyPartCost(int id) throws SQLException{
        PreparedStatement getReplacementPartCosts = query("SELECT\n" +  
        "[Booking].booking_id AS bID, [Parts].cost AS pCost, count(*)\n" +
        "FROM\n" +
        "[Booking]\n" +
        "JOIN ([PartsInstalled] inner join [Parts] ON [PartsInstalled].part_id = [Parts].id)[PartsInstalled] ON [booking].booking_id = [PartsInstalled].booking_id\n" +
        "WHERE [PartsInstalled].waranty_expiration_date > date('now')\n" +
        "GROUP BY\n" +
        "[Booking].vehicle_id, [Parts].id\n" +
        "HAVING\n" +
        "count(*)>1");
        
        List<Integer> WarrantyParts = new ArrayList<>();
        List<Double> partCosts = new ArrayList<>();
        ResultSet rs = getReplacementPartCosts.executeQuery();
        while (rs.next()) {
             WarrantyParts.add(rs.getInt("bID"));
             partCosts.add(rs.getDouble("pCost"));
         }
        double totalPartCostUnderWaranty = 0;
         
             for(int i=0;i<WarrantyParts.size();i++)
             {
                if(WarrantyParts.contains(id))
                {
                    totalPartCostUnderWaranty = totalPartCostUnderWaranty + partCosts.get(i);
                }
             }
         
        return totalPartCostUnderWaranty;
    }
    

    public Collection<Customer> getCustomersWithNameForParts(String FirstLastName) throws SQLException {
        PreparedStatement getAllCustomersWithName = query("SELECT * FROM `Customer` WHERE full_name LIKE ?");
        String x = "%" + FirstLastName + "%";
        // Execute query
        getAllCustomersWithName.setString(1, x);
        ResultSet rs = getAllCustomersWithName.executeQuery();

        // Create corresponding customer instances
        Collection<Customer> listOfCustomersWithName = new ArrayList<>();

        while (rs.next()) {
            // Get corresponding info
            int CustomerId = rs.getInt("id");
            String name = rs.getString("full_name");
            String phone = rs.getString("phone");
            String email = rs.getString("email_address");
                    String city = rs.getString("city");

            String streetAddr = rs.getString("street_address");
CustomerType type;
      if(rs.getString("type").equals("Private")){
            type = CustomerType.Private;
        }
        else{
             type = CustomerType.Business;
        }
        String postcode = rs.getString("postcode");

            // Create customer object and add to list
            Customer customer = new Customer(CustomerId, name, phone, email,city, streetAddr, postcode, type);
            listOfCustomersWithName.add(customer);
        }
        return listOfCustomersWithName;
    }

    public List<Transactions> getAllPartDeliveries() throws SQLException {
        PreparedStatement getAllPartTransactionStatement = query("SELECT transaction_id, Part_id, Name, Description, Quantity_change, is_incoming FROM PartTransactions INNER JOIN Parts\n" +
                                                                 "ON Parts.id = PartTransactions.Part_id WHERE is_incoming = 1;");

        // Execute query
        ResultSet rs = getAllPartTransactionStatement.executeQuery();

        // Create corresponding part instances
        List<Transactions> listOfPartTransactions = new ArrayList<>();

        while (rs.next()) {
            // Get corresponding info
            int ID = rs.getInt("Part_id");
            String Name = rs.getString("Name");
            String Description = rs.getString("Description");
            int tID = rs.getInt("transaction_id");
            int Quantity = rs.getInt("Quantity_change");
            boolean incoming = rs.getBoolean("is_incoming");

            // Create part object and add to list
            Transactions Transactions = new Transactions(ID,Name,Description, tID, Quantity, incoming);
            listOfPartTransactions.add(Transactions);
        }
        return listOfPartTransactions;
    }

    public Transactions getPartDeliveryWithID(int tempID) throws SQLException {
        PreparedStatement getAllPartTransactionStatement = query("SELECT transaction_id, Part_id, Name, Description, Quantity_change, is_incoming FROM PartTransactions INNER JOIN Parts\n" +
        "ON Parts.id = PartTransactions.Part_id WHERE is_incoming = 1 AND Transaction_id = ?;");
        getAllPartTransactionStatement.setInt(1, tempID);

        // Execute query
        ResultSet rs = getAllPartTransactionStatement.executeQuery();

            int tID = rs.getInt("transaction_id");
            int ID = rs.getInt("Part_id");
            String Name = rs.getString("Name");
            String Description = rs.getString("Description");
            int Quantity = rs.getInt("Quantity_change");
            boolean incoming = rs.getBoolean("is_incoming");

            Transactions Transactions = new Transactions(ID,Name,Description, tID, Quantity, incoming);

        return Transactions;
    }

   public List<PartWithdrawals> getAllPartWithdrawals() throws SQLException, ParseException {
        PreparedStatement getAllPartTransactionStatement = query("SELECT\n" +
        "[Booking].start_date AS bStartDate,\n" +
        "[Booking].end_date AS bEndDate,\n" +
        "[Booking].vehicle_id AS VehicleReg,\n" +
        "[Customer].full_name AS CustName,\n" +
        "[Parts].name AS PartName,\n" +
        "[Parts].id AS Partid,\n" +
        "[Parts].cost AS PartCost,\n" +
        "[PartsInstalled].id AS partinstallID,\n" +
        "[PartsInstalled].quantity_installed AS quantity,\n" +
        "[PartsInstalled].booking_id AS bookid,\n" +
        "[PartsInstalled].type AS type\n" +
        "FROM [booking]\n" +
        "JOIN [Customer] ON [booking].customer_id = [Customer].id\n" +
        "JOIN ([PartsInstalled] inner join [Parts] ON [PartsInstalled].part_id = [Parts].id)[PartsInstalled] ON [booking].booking_id = [PartsInstalled].booking_id");

        // Execute query
        ResultSet rs = getAllPartTransactionStatement.executeQuery();

        // Create corresponding part instances
        List<PartWithdrawals> listOfPartWithdrawals = new ArrayList<>();

        while (rs.next()) {
            // Get corresponding info
            int ID = rs.getInt("partinstallID");
            int bID = rs.getInt("bookid");
            String startdate = rs.getString("bStartDate");
            SimpleDateFormat curFormater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            java.util.Date sdateObj = curFormater.parse(startdate);
            Calendar bstartDate = Calendar.getInstance();
            bstartDate.setTime(sdateObj);
            String enddate = rs.getString("bEndDate");
            java.util.Date edateObj = curFormater.parse(enddate);
            Calendar bEndDate = Calendar.getInstance();
            bEndDate.setTime(edateObj);
            String vehicleReg = rs.getString("VehicleReg");
            String custName = rs.getString("CustName");
            String PartName = rs.getString("PartName");
            int PartID = rs.getInt("Partid");
            Double partCost = rs.getDouble("PartCost");
            int quantity = rs.getInt("quantity");
            int type = rs.getInt("type");
            // Create part object and add to list
            PartWithdrawals pw = new PartWithdrawals(ID,bID, bstartDate, vehicleReg, custName, PartID, PartName, partCost, quantity, type);
            listOfPartWithdrawals.add(pw);
        }
        return listOfPartWithdrawals;
    }
   
   public List<PartWithdrawals> getPartWithdrawalsOnly() throws SQLException, ParseException {
        PreparedStatement getAllPartTransactionStatement = query("SELECT\n" +
        "[Booking].start_date AS bStartDate,\n" +
        "[Booking].end_date AS bEndDate,\n" +
        "[Booking].vehicle_id AS VehicleReg,\n" +
        "[Customer].full_name AS CustName,\n" +
        "[Parts].name AS PartName,\n" +
        "[Parts].id AS Partid,\n" +
        "[Parts].cost AS PartCost,\n" +
        "[PartsInstalled].id AS partinstallID,\n" +
        "[PartsInstalled].quantity_installed AS quantity,\n" +
        "[PartsInstalled].booking_id AS bookid,\n" +
        "[PartsInstalled].type AS type\n" +
        "FROM [booking]\n" +
        "JOIN [Customer] ON [booking].customer_id = [Customer].id\n" +
        "JOIN ([PartsInstalled] inner join [Parts] ON [PartsInstalled].part_id = [Parts].id)[PartsInstalled] ON [booking].booking_id = [PartsInstalled].booking_id\n" +
        "WHERE [PartsInstalled].type = 1;");

        // Execute query
        ResultSet rs = getAllPartTransactionStatement.executeQuery();

        // Create corresponding part instances
        List<PartWithdrawals> listOfPartWithdrawals = new ArrayList<>();

        while (rs.next()) {
            // Get corresponding info
            int ID = rs.getInt("partinstallID");
            int bID = rs.getInt("bookid");
            String startdate = rs.getString("bStartDate");
            SimpleDateFormat curFormater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            java.util.Date sdateObj = curFormater.parse(startdate);
            Calendar bstartDate = Calendar.getInstance();
            bstartDate.setTime(sdateObj);
            String enddate = rs.getString("bEndDate");
            java.util.Date edateObj = curFormater.parse(enddate);
            Calendar bEndDate = Calendar.getInstance();
            bEndDate.setTime(edateObj);
            String vehicleReg = rs.getString("VehicleReg");
            String custName = rs.getString("CustName");
            String PartName = rs.getString("PartName");
            int PartID = rs.getInt("Partid");
            Double partCost = rs.getDouble("PartCost");
            int quantity = rs.getInt("quantity");
            int type = rs.getInt("type");
            // Create part object and add to list
            PartWithdrawals pw = new PartWithdrawals(ID,bID, bstartDate, vehicleReg, custName, PartID, PartName, partCost, quantity, type);
            listOfPartWithdrawals.add(pw);
        }
        return listOfPartWithdrawals;
    }
   
   public List<PartWithdrawals> getPartWithdrawalsWithID(int id) throws SQLException, ParseException {
        PreparedStatement getAllPartTransactionStatement = query("SELECT\n" +
        "[Booking].start_date AS bStartDate,\n" +
        "[Booking].end_date AS bEndDate,\n" +
        "[Booking].vehicle_id AS VehicleReg,\n" +
        "[Customer].full_name AS CustName,\n" +
        "[Parts].name AS PartName,\n" +
        "[Parts].id AS Partid,\n" +
        "[Parts].cost AS PartCost,\n" +
        "[PartsInstalled].id AS partinstallID,\n" +
        "[PartsInstalled].quantity_installed AS quantity,\n" +
        "[PartsInstalled].booking_id AS bookid,\n" +
        "[PartsInstalled].type AS type\n" +
        "FROM [booking]\n" +
        "JOIN [Customer] ON [booking].customer_id = [Customer].id\n" +
        "JOIN ([PartsInstalled] inner join [Parts] ON [PartsInstalled].part_id = [Parts].id)[PartsInstalled] ON [booking].booking_id = [PartsInstalled].booking_id WHERE [PartsInstalled].id = ?");
        getAllPartTransactionStatement.setInt(1, id);
        // Execute query
        ResultSet rs = getAllPartTransactionStatement.executeQuery();

        // Create corresponding part instances
        List<PartWithdrawals> listOfPartWithdrawals = new ArrayList<>();

        while (rs.next()) {
            // Get corresponding info
            int ID = rs.getInt("partinstallID");
            int bID = rs.getInt("bookid");
            String startdate = rs.getString("bStartDate");
            SimpleDateFormat curFormater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            java.util.Date sdateObj = curFormater.parse(startdate);
            Calendar bstartDate = Calendar.getInstance();
            bstartDate.setTime(sdateObj);
            String enddate = rs.getString("bEndDate");
            java.util.Date edateObj = curFormater.parse(enddate);
            Calendar bEndDate = Calendar.getInstance();
            bEndDate.setTime(edateObj);
            String vehicleReg = rs.getString("VehicleReg");
            String custName = rs.getString("CustName");
            String PartName = rs.getString("PartName");
            int PartID = rs.getInt("Partid");
            Double partCost = rs.getDouble("PartCost");
            int quantity = rs.getInt("quantity");
            int type = rs.getInt("type");
            // Create part object and add to list
            PartWithdrawals pw = new PartWithdrawals(ID,bID, bstartDate, vehicleReg, custName, PartID, PartName, partCost, quantity, type);
            listOfPartWithdrawals.add(pw);
        }
        return listOfPartWithdrawals;
    }


    public List<InstalledParts> getPartsInstalledWithID(int id) throws SQLException, ParseException {
        PreparedStatement getPartsInstalledStatement = query("SELECT\n" +
            "[Booking].start_date AS BookingDate,\n" +
            "[Booking].vehicle_id AS VehicleReg,\n" +
            "[Customer].full_name AS CustName,\n" +
            "[Parts].name AS PartName,\n" +
            "[Parts].id AS PartID,\n" +
            "[Parts].cost AS PartCost,\n" +
            "[Parts].description AS description,\n" +
            "[PartsInstalled].id AS partinstallID,\n" +
            "[PartsInstalled].quantity_installed AS QuantityInstalled,\n" +
            "[PartsInstalled].install_date AS InstalledDate,\n" +
            "[PartsInstalled].booking_id AS bookid,\n" +
            "[PartsInstalled].waranty_expiration_date AS warantyExpiryDate\n" +
            "FROM [booking]\n" +
            "JOIN [Customer] ON [booking].customer_id = [Customer].id\n" +
            "JOIN ([PartsInstalled] inner join [Parts] ON [PartsInstalled].part_id = [Parts].id)[PartsInstalled] ON [booking].booking_id = [PartsInstalled].booking_id WHERE [PartsInstalled].id = ?");
        getPartsInstalledStatement.setInt(1, id);
        ResultSet rs = getPartsInstalledStatement.executeQuery();

        List<InstalledParts> listOfPartsInstalled = new ArrayList<>();

        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");

        while (rs.next()) {
            // Get corresponding info
            int partinstallID = rs.getInt("partinstallID");
            java.util.Date bookDate = format.parse(rs.getString("BookingDate"));
            int ID = rs.getInt("PartID");
            String vehicleReg = rs.getString("VehicleReg");
            String description = rs.getString("Description");
            String custName = rs.getString("CustName");
            String partName = rs.getString("PartName");
            int quantityInstalled = rs.getInt("QuantityInstalled");
            int BookID = rs.getInt("bookid");
            String installDate = rs.getString("InstalledDate");
            String expiryDate = rs.getString("warantyExpiryDate");
            Double cost = rs.getDouble("PartCost");
            Calendar bookedDate = Calendar.getInstance();
            bookedDate.setTime(bookDate);
            SimpleDateFormat curFormater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            java.util.Date idateObj = curFormater.parse(installDate);
            java.util.Date edateObj = curFormater.parse(expiryDate);
            Calendar InstallDate = Calendar.getInstance();
            InstallDate.setTime(idateObj);
            Calendar ExpiryDate = Calendar.getInstance();
            ExpiryDate.setTime(edateObj);

            // Create part object and add to list
            InstalledParts InstalledParts = new InstalledParts(ID,partName,description,bookedDate, vehicleReg, custName,InstallDate,ExpiryDate, quantityInstalled, BookID, partinstallID,cost);
            listOfPartsInstalled.add(InstalledParts);
        }
        return listOfPartsInstalled;

    }
        public void deletePartDelivery(int id) throws SQLException{
        PreparedStatement delStatement = query("DELETE FROM [PartTransactions] WHERE [PartTransactions].Transaction_id = ?");
        delStatement.setInt(1, id);
        delStatement.executeUpdate();
        updatePartTransactionsID(id);
    }
        public void deleteStockPart(int id) throws SQLException{
        PreparedStatement delStatement = query("DELETE FROM [Parts] WHERE [Parts].id = ?");
        delStatement.setInt(1, id);
        delStatement.executeUpdate();
        updateStockPartID(id);
    }
        public void updateStockPartID(int id) throws SQLException{
        PreparedStatement updateIDStatement = query("UPDATE 'Parts' SET id = id-1 WHERE id> ?");
        updateIDStatement.setInt(1, id);
        updateIDStatement.executeUpdate();
    }
        
        public boolean checkUsedStockPart(int id) throws SQLException{
        PreparedStatement checkStockPartStatement = query("SELECT [PartsInstalled].part_id FROM [PartsInstalled]");
        ResultSet rs = checkStockPartStatement.executeQuery();
        List<Integer> ids = new ArrayList();
        while(rs.next()){
            ids.add(rs.getInt("part_id"));
        }
        boolean check = false;
        if(ids.contains(id))
        {
            check = true;
        }
        return check;
    }
    
    public void updatePartTransactionsID(int id) throws SQLException{
        PreparedStatement updateIDStatement = query("UPDATE 'PartTransactions' SET Transaction_id = Transaction_id-1 WHERE Transaction_id> ?");
        updateIDStatement.setInt(1, id);
        updateIDStatement.executeUpdate();
    }

    public void deletePartsInstalled(int id) throws SQLException{
        PreparedStatement delStatement = query("DELETE FROM [PartsInstalled] WHERE [PartsInstalled].id = ?");
        delStatement.setInt(1, id);
        delStatement.executeUpdate();
        updatePartsInstalledID(id);
    }

    public void updatePartsInstalledID (int id) throws SQLException{
        PreparedStatement updateIDStatement = query("UPDATE 'PartsInstalled' SET id = id-1 WHERE id>?");
        updateIDStatement.setInt(1, id);
        updateIDStatement.executeUpdate();
    }

    public void updateStockPartsID (int id) throws SQLException{
        PreparedStatement updateIDStatement = query("UPDATE 'Parts' SET id = id-1 WHERE id>?");
        updateIDStatement.setInt(1, id);
        updateIDStatement.executeUpdate();
    }
    
    public List<Integer> getPartsInstalledIDs()throws SQLException {
        PreparedStatement getIDStatement = query("SELECT\n" +
        "[PartsInstalled].id AS partinstallID\n" +
        "FROM [booking]\n" +
        "JOIN [Customer] ON [booking].customer_id = [Customer].id\n" +
        "JOIN ([PartsInstalled] inner join [Parts] ON [PartsInstalled].part_id = [Parts].id)[PartsInstalled] ON [booking].booking_id = [PartsInstalled].booking_id WHERE [PartsInstalled].type = 1;");
        ResultSet rs = getIDStatement.executeQuery();
        List<Integer> ids = new ArrayList();
        while(rs.next()){
            ids.add(rs.getInt("partinstallID"));
        }
        return ids;
    }
    
    public int checkPartQuantity(String name) throws SQLException {
        PreparedStatement getQuantity = query("SELECT [Parts].quantity FROM Parts WHERE [Parts].name = ?");
        getQuantity.setString(1,name);
        ResultSet rs = getQuantity.executeQuery();
        int quantity = rs.getInt("quantity");
        return quantity;
    }

    public List<InstalledParts> getAllPartsInstalled() throws SQLException, ParseException {
        PreparedStatement getAllPartInstalledStatement = query("SELECT\n" +
            "[Booking].start_date AS BookingDate,\n" +
            "[Booking].vehicle_id AS VehicleReg,\n" +
            "[Customer].full_name AS CustName,\n" +
            "[Parts].name AS PartName,\n" +
            "[Parts].id AS PartID,\n" +
            "[Parts].cost AS PartCost,\n" +
            "[Parts].description AS description,\n" +
            "[PartsInstalled].id AS partinstallID,\n" +
            "[PartsInstalled].quantity_installed AS QuantityInstalled,\n" +
            "[PartsInstalled].install_date AS InstalledDate,\n" +
            "[PartsInstalled].booking_id AS bookid,\n" +
            "[PartsInstalled].waranty_expiration_date AS warantyExpiryDate\n" +
            "FROM [booking]\n" +
            "JOIN [Customer] ON [booking].customer_id = [Customer].id\n" +
            "JOIN ([PartsInstalled] inner join [Parts] ON [PartsInstalled].part_id = [Parts].id)[PartsInstalled] ON [booking].booking_id = [PartsInstalled].booking_id WHERE [PartsInstalled].type = 1;");
        // Execute query
        ResultSet rs = getAllPartInstalledStatement.executeQuery();

        // Create corresponding part instances
        List<InstalledParts> listOfPartsInstalled = new ArrayList<>();

        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");

        while (rs.next()) {
            // Get corresponding info
            int partinstallID = rs.getInt("partinstallID");
            java.util.Date bookDate = format.parse(rs.getString("BookingDate"));
            int ID = rs.getInt("PartID");
            String vehicleReg = rs.getString("VehicleReg");
            String description = rs.getString("Description");
            String custName = rs.getString("CustName");
            String partName = rs.getString("PartName");
            int quantityInstalled = rs.getInt("QuantityInstalled");
            int BookID = rs.getInt("bookid");
            String installDate = rs.getString("InstalledDate");
            String expiryDate = rs.getString("warantyExpiryDate");
            Double cost = rs.getDouble("PartCost");
            Calendar bookedDate = Calendar.getInstance();
            bookedDate.setTime(bookDate);
            SimpleDateFormat curFormater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            java.util.Date idateObj = curFormater.parse(installDate);
            java.util.Date edateObj = curFormater.parse(expiryDate);
            Calendar InstallDate = Calendar.getInstance();
            InstallDate.setTime(idateObj);
            Calendar ExpiryDate = Calendar.getInstance();
            ExpiryDate.setTime(edateObj);

            // Create part object and add to list
            InstalledParts InstalledParts = new InstalledParts(ID,partName,description,bookedDate, vehicleReg, custName,InstallDate,ExpiryDate, quantityInstalled, BookID,partinstallID,cost);
            listOfPartsInstalled.add(InstalledParts);
        }
        return listOfPartsInstalled;
    }

    public int getBookingIDWithVehicleReg(String vehicle) throws SQLException{
        PreparedStatement getBookingIDStatement = query("SELECT [booking].booking_id FROM [booking] WHERE [booking].vehicle_id = ?");
        getBookingIDStatement.setString(1, vehicle);
        ResultSet rs = getBookingIDStatement.executeQuery();
        int bookingID = rs.getInt("booking_id");
        return bookingID;
    }
    /*public boolean checkForDuplicatingInstalledPartsInSPCBooking(int bookingId) throws SQLException{
        //TO BE CHANGED, THIS DOESNT WORK
        PreparedStatement getPartsInstalledStatement = query("SELECT COUNT (id) as total FROM PartsInstalled WHERE booking_id = ?");
        getPartsInstalledStatement.setInt(1,bookingId);
        return getPartsInstalledStatement.executeQuery().getInt("total") > 0;
    }*/
    public boolean checkForDuplicatingInstalledPartsInSPCBooking(int id) throws SQLException{
        PreparedStatement getPartsInstalledStatement = query("SELECT COUNT (id) as total FROM SPCParts WHERE installed_part_id = ?");
        getPartsInstalledStatement.setInt(1,id);
        return getPartsInstalledStatement.executeQuery().getInt("total") > 0;
    }
    public List<InstalledParts> getPartsInstalledWithVehicleReg(String vehicle) throws SQLException, ParseException {
        PreparedStatement getPartsInstalledWithVehicleStatement = query("SELECT\n" +
            "[Booking].start_date AS BookingDate,\n" +
            "[Booking].vehicle_id AS VehicleReg,\n" +
            "[Customer].full_name AS CustName,\n" +
            "[Parts].name AS PartName,\n" +
            "[Parts].id AS PartID,\n" +
            "[Parts].cost AS PartCost,\n" +
            "[Parts].description AS description,\n" +
            "[PartsInstalled].id AS partinstallID,\n" +
            "[PartsInstalled].quantity_installed AS QuantityInstalled,\n" +
            "[PartsInstalled].install_date AS InstalledDate,\n" +
            "[PartsInstalled].booking_id AS bookid,\n" +
            "[PartsInstalled].waranty_expiration_date AS warantyExpiryDate\n" +
            "FROM [booking]\n" +
            "JOIN [Customer] ON [booking].customer_id = [Customer].id\n" +
            "JOIN ([PartsInstalled] inner join [Parts] ON [PartsInstalled].part_id = [Parts].id)[PartsInstalled] ON [booking].booking_id = [PartsInstalled].booking_id\n" +
            "WHERE [PartsInstalled].type = 1 AND [booking].vehicle_id LIKE ?");
        String input = "%" + vehicle + "%";
        getPartsInstalledWithVehicleStatement.setString(1,input);
        // Execute query
        ResultSet rs = getPartsInstalledWithVehicleStatement.executeQuery();

        // Create corresponding part instances
        List<InstalledParts> listOfPartsInstalled = new ArrayList<>();

        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");

        while (rs.next()) {
            // Get corresponding info
            int partinstallID = rs.getInt("partinstallID");
            java.util.Date bookDate = format.parse(rs.getString("BookingDate"));
            int ID = rs.getInt("PartID");
            String vehicleReg = rs.getString("VehicleReg");
            String description = rs.getString("Description");
            String custName = rs.getString("CustName");
            String partName = rs.getString("PartName");
            int quantityInstalled = rs.getInt("QuantityInstalled");
            int BookID = rs.getInt("bookid");
            String installDate = rs.getString("InstalledDate");
            String expiryDate = rs.getString("warantyExpiryDate");
            Double cost = rs.getDouble("PartCost");
            Calendar bookedDate = Calendar.getInstance();
            bookedDate.setTime(bookDate);
            SimpleDateFormat curFormater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            java.util.Date idateObj = curFormater.parse(installDate);
            java.util.Date edateObj = curFormater.parse(expiryDate);
            Calendar InstallDate = Calendar.getInstance();
            InstallDate.setTime(idateObj);
            Calendar ExpiryDate = Calendar.getInstance();
            ExpiryDate.setTime(edateObj);

            // Create part object and add to list
            InstalledParts InstalledParts = new InstalledParts(ID,partName,description,bookedDate, vehicleReg, custName,InstallDate,ExpiryDate, quantityInstalled, BookID,partinstallID,cost);
            listOfPartsInstalled.add(InstalledParts);
        }
        return listOfPartsInstalled;
    }

    public Collection<Customer> getCustomersWithVehicleReg(String VehicleReg) throws SQLException{
        PreparedStatement getCustomersWithVehicleReg = query("SELECT Customer.*\n" +
        "FROM [Vehicle]\n" +
        "JOIN [Customer] ON [Vehicle].customer_id = [Customer].id\n" +
        "WHERE [Vehicle].RegistrationNo LIKE ?");
        String input = "%" + VehicleReg + "%";
        getCustomersWithVehicleReg.setString(1,input);
        ResultSet rs = getCustomersWithVehicleReg.executeQuery();
        Collection<Customer> listOfCustomersWithName = new ArrayList<>();

        while (rs.next()) {
            // Get corresponding info
            int CustomerId = rs.getInt("id");
            String name = rs.getString("full_name");
            String phone = rs.getString("phone");
            String email = rs.getString("email_address");
                    String city = rs.getString("city");

            String streetAddr = rs.getString("street_address");
CustomerType type;
        if(rs.getString("type").equals("Private")){
            type = CustomerType.Private;
        }
        else{
             type = CustomerType.Business;
        }            String postcode = rs.getString("postcode");

            // Create customer object and add to list
            Customer customer = new Customer(CustomerId, name, phone, email,city, streetAddr, postcode, type);
            listOfCustomersWithName.add(customer);
        }
        return listOfCustomersWithName;
    }

    public List<Vehicle> getVehiclesWithCustomerName(String Name) throws SQLException, ParseException{
        PreparedStatement getVehiclesWithCustName = query("SELECT Vehicle.*\n" +
            "FROM [Vehicle]\n" +
            "JOIN [Customer] ON [Vehicle].customer_id = [Customer].id\n" +
            "WHERE [Customer].full_name LIKE ?");
        String input = "%" + Name + "%";
        getVehiclesWithCustName.setString(1, input);
        ResultSet rs = getVehiclesWithCustName.executeQuery();

        // Create corresponding SPC instances
        List<Vehicle> listOfVehicles = new ArrayList<>();

        while (rs.next()) {
            // Get corresponding info
            String RegNo = rs.getString("RegistrationNo");
            int VehicleTemplateID = rs.getInt("VehicleTemplateId");
            VehicleTemplate template = Database.getInstance().getTemplate(VehicleTemplateID);
            String Colour = rs.getString("Colour");
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            java.util.Date MotDate = df.parse(rs.getString("MOTRenewalDate"));
            Calendar MOTRenewalDate = Calendar.getInstance();
            MOTRenewalDate.setTime(MotDate);
            boolean Warranty = rs.getInt("Warranty") == 1;
            java.util.Date LastServDate = df.parse(rs.getString("LastServiceDate"));
            Calendar LastServiceDate = Calendar.getInstance();
            LastServiceDate.setTime(LastServDate);
            double CurrentMileage = rs.getDouble("CurrentMileage");
            int Customerid = rs.getInt("customer_id");
            Customer CustomerID = Database.getInstance().getCustomer(Customerid);
            int WarrantyID = rs.getInt("WarrantyID");
            Warranty warranty = Database.getInstance().getWarrantyforVehicle(WarrantyID);
            listOfVehicles.add(new Vehicle(RegNo, template, Colour, MOTRenewalDate, Warranty,LastServiceDate, CurrentMileage,CustomerID,warranty));
        }
        return listOfVehicles;
    }

    public List<InstalledParts> getPartsInstalledWithCustomerName(String name) throws SQLException, ParseException {
        PreparedStatement getPartsInstalledWithCust = query("SELECT\n" +
            "[Booking].start_date AS BookingDate,\n" +
            "[Booking].vehicle_id AS VehicleReg,\n" +
            "[Customer].full_name AS CustName,\n" +
            "[Parts].name AS PartName,\n" +
            "[Parts].id AS PartID,\n" +
            "[Parts].cost AS PartCost,\n" +
            "[Parts].description AS description,\n" +
            "[PartsInstalled].id AS partinstallID,\n" +
            "[PartsInstalled].quantity_installed AS QuantityInstalled,\n" +
            "[PartsInstalled].install_date AS InstalledDate,\n" +
            "[PartsInstalled].booking_id AS bookid,\n" +
            "[PartsInstalled].waranty_expiration_date AS warantyExpiryDate\n" +
            "FROM [booking]\n" +
            "JOIN [Customer] ON [booking].customer_id = [Customer].id\n" +
            "JOIN ([PartsInstalled] inner join [Parts] ON [PartsInstalled].part_id = [Parts].id)[PartsInstalled] ON [booking].booking_id = [PartsInstalled].booking_id\n" +
            "WHERE [PartsInstalled].type = 1 AND [Customer].full_name LIKE ?");
        String input = "%" + name + "%";
        getPartsInstalledWithCust.setString(1,input);
        // Execute query
        ResultSet rs = getPartsInstalledWithCust.executeQuery();

        // Create corresponding part instances
        List<InstalledParts> listOfPartsInstalled = new ArrayList<>();

        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");

        while (rs.next()) {
            // Get corresponding info
            int partinstallID = rs.getInt("partinstallID");
            java.util.Date bookDate = format.parse(rs.getString("BookingDate"));
            int ID = rs.getInt("PartID");
            String vehicleReg = rs.getString("VehicleReg");
            String description = rs.getString("Description");
            String custName = rs.getString("CustName");
            String partName = rs.getString("PartName");
            int quantityInstalled = rs.getInt("QuantityInstalled");
            int BookID = rs.getInt("bookid");
            String installDate = rs.getString("InstalledDate");
            String expiryDate = rs.getString("warantyExpiryDate");
            Double cost = rs.getDouble("PartCost");
            Calendar bookedDate = Calendar.getInstance();
            bookedDate.setTime(bookDate);
            SimpleDateFormat curFormater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            java.util.Date idateObj = curFormater.parse(installDate);
            java.util.Date edateObj = curFormater.parse(expiryDate);
            Calendar InstallDate = Calendar.getInstance();
            InstallDate.setTime(idateObj);
            Calendar ExpiryDate = Calendar.getInstance();
            ExpiryDate.setTime(edateObj);

            // Create part object and add to list
            InstalledParts InstalledParts = new InstalledParts(ID,partName,description,bookedDate, vehicleReg, custName,InstallDate,ExpiryDate, quantityInstalled, BookID, partinstallID,cost);
            listOfPartsInstalled.add(InstalledParts);
        }
        return listOfPartsInstalled;
    }

    public List<String> getAllVehicleRegFromBookings() throws SQLException {
        PreparedStatement getAllVehicleRegsStatement = query("SELECT [booking].vehicle_id FROM [booking] WHERE [booking].booking_type = 1 AND [booking].end_date > date('now');");
        List<String> vehicleRegs = new ArrayList<>();
        ResultSet rs = getAllVehicleRegsStatement.executeQuery();

        while(rs.next()){
            vehicleRegs.add(rs.getString("vehicle_id"));
        }
        return vehicleRegs;
    }

    public Vehicle getVehicleWithPartialReg(String RegistrationNo) throws SQLException, ParseException {
        PreparedStatement getVehicleStatement

        = query("SELECT RegistrationNo,VehicleTemplateID,Colour,WarrantyID,MotRenewalDate,Warranty,LastServiceDate,CurrentMileage,customer_id FROM `Vehicle` WHERE `RegistrationNo` LIKE ?;");
        String input = "%" + RegistrationNo + "%";
        getVehicleStatement.setString(1, input);
        ResultSet rs = getVehicleStatement.executeQuery() ;

            String RegNo = rs.getString("RegistrationNo");
            int VehicleTemplateID = rs.getInt("VehicleTemplateId");
            VehicleTemplate template = Database.getInstance().getTemplate(VehicleTemplateID);
            String Colour = rs.getString("Colour");
            DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
            java.util.Date MotDate = df.parse(rs.getString("MOTRenewalDate"));
            Calendar MOTRenewalDate = Calendar.getInstance();
            MOTRenewalDate.setTime(MotDate);
            boolean Warranty = rs.getInt("Warranty") == 1;
            java.util.Date LastServDate = df.parse(rs.getString("LastServiceDate"));
            Calendar LastServiceDate = Calendar.getInstance();
            LastServiceDate.setTime(LastServDate);
            double CurrentMileage = rs.getDouble("CurrentMileage");
            int Customerid = rs.getInt("customer_id");
            Customer CustomerID = Database.getInstance().getCustomer(Customerid);
            int WarrantyID = rs.getInt("WarrantyID");
            Warranty warranty = Database.getInstance().getWarrantyforVehicle(WarrantyID);
        return new Vehicle(RegNo, template, Colour, MOTRenewalDate, Warranty, LastServiceDate, CurrentMileage,CustomerID,warranty);

    }


    public List<String> getPartNames() throws SQLException {
        List<String> PartNames = new ArrayList<>();
        PreparedStatement getPartNameStatement = query("SELECT [Parts].name FROM [Parts]");
        ResultSet rs = getPartNameStatement.executeQuery();

        while(rs.next()) {
            PartNames.add(rs.getString("name"));

        }
        return PartNames;
    }

    public List<Integer> getPartIDs() throws SQLException {
        List<Integer> PartIDs = new ArrayList<>();
        PreparedStatement getPartNameStatement = query("SELECT [Parts].id FROM [Parts]");
        ResultSet rs = getPartNameStatement.executeQuery();

        while(rs.next()) {
            PartIDs.add(rs.getInt("id"));

        }
        return PartIDs;
    }

    public List<Integer> getBookingIDs() throws SQLException {
        List<Integer> BookingIDs = new ArrayList<>();
        PreparedStatement getBookingIDsStatement = query("SELECT [booking].booking_id FROM [booking] WHERE [booking].booking_type = 1 AND [booking].end_date > date('now');");
        ResultSet rs = getBookingIDsStatement.executeQuery();

        while(rs.next()){
            BookingIDs.add(rs.getInt("booking_id"));
        }
        return BookingIDs;
    }

    public List<String> getBookingDetails(int id) throws SQLException {
        List<String> bookDetails = new ArrayList<>();
        PreparedStatement getbookDetailsStatement = query("SELECT [Booking].start_date AS BookingDate,\n" +
            "[Vehicle].RegistrationNo AS VehicleReg,\n" +
            "[Customer].full_name AS CustName\n" +
            "FROM [booking]\n" +
            "JOIN [Vehicle] ON [booking].vehicle_id = [Vehicle].RegistrationNo\n" +
            "JOIN [Customer] ON [booking].customer_id = [Customer].id\n" +
            "WHERE [booking].booking_id IS ?;");
        getbookDetailsStatement.setInt(1,id);
        ResultSet rs = getbookDetailsStatement.executeQuery();
        String bookDate = rs.getString("BookingDate");
        String reg = (Integer.toString(rs.getInt("VehicleReg")));
        String CustName = rs.getString("CustName");
        bookDetails.add(bookDate);
        bookDetails.add(reg);
        bookDetails.add(CustName);
        return bookDetails;
    }

    public List<String> getPartDetails(String Name) throws SQLException {
        List<String> PartDetails = new ArrayList<>();
        PreparedStatement getPartDetailsStatement = query("SELECT id, description FROM    'Parts'  WHERE name IS ?;");
        getPartDetailsStatement.setString(1,Name);
        ResultSet rs = getPartDetailsStatement.executeQuery();
        int id = rs.getInt("id");
        String description = rs.getString("description");
        PartDetails.add(Integer.toString(id));
        PartDetails.add(description);
        return PartDetails;
    }


    public StockParts getStockPartForId(int partId) throws SQLException {
        PreparedStatement getPartForIdStatement = query("SELECT * FROM `Parts` WHERE id=?");
        getPartForIdStatement.setInt(1, partId);

        // Execute query
        ResultSet rs = getPartForIdStatement.executeQuery();

        // Get corresponding info
        int id = rs.getInt("id");
        String name = rs.getString("name");
        String description = rs.getString("description");
        int quantity = rs.getInt("quantity");
        double cost = rs.getDouble("cost");

        // Create part object and add to list
        return new StockParts(id, name, description, quantity, cost);
    }

    /**
     * the sql query to get a resultset to pass onto vehiclesFromResultSet(ResultSet rs)
     */
    public ArrayList<Vehicle> getAllVehicles() throws SQLException, ParseException {
        PreparedStatement getAllVehiclesStatement = query("SELECT * FROM `Vehicle`");
        return vehiclesFromResultSet(getAllVehiclesStatement.executeQuery());
    }
    public ArrayList<Vehicle> getAllVehicleswithRegNo(String RegNo) throws SQLException, ParseException {
        PreparedStatement getAllVehiclesStatement = query("SELECT * FROM `Vehicle` WHERE RegistrationNo LIKE ?  COLLATE NOCASE");
        getAllVehiclesStatement.setString(1, "%" + RegNo + "%");
        return vehiclesFromResultSet(getAllVehiclesStatement.executeQuery());
    }
    public ArrayList<Vehicle> getAllVehicleswithMake(String Make) throws SQLException, ParseException {
        PreparedStatement getAllVehiclesStatement = query("SELECT * FROM 'Vehicle' NATURAL JOIN 'VehicleTemplate' WHERE 'VehicleTemplate'.Make LIKE ? COLLATE NOCASE");
        getAllVehiclesStatement.setString(1, "%" + Make + "%");
        return vehiclesFromResultSet(getAllVehiclesStatement.executeQuery());
    }
    public ArrayList<Vehicle> getAllVehicleswithVehicleTemplateID(int vehicletemplateid) throws SQLException, ParseException {
        PreparedStatement getAllVehiclesStatement = query("SELECT * FROM `Vehicle` WHERE VehicleTemplateId = ? ");
        getAllVehiclesStatement.setInt(1, vehicletemplateid);
        return vehiclesFromResultSet(getAllVehiclesStatement.executeQuery());
    }
    public ArrayList<Vehicle> getAllVehicleswithType(String Type) throws SQLException, ParseException {
        PreparedStatement getAllVehiclesStatement = query("SELECT * FROM 'Vehicle' NATURAL JOIN 'VehicleTemplate' WHERE 'VehicleTemplate'.VehicleType LIKE ? COLLATE NOCASE");
        getAllVehiclesStatement.setString(1, "%" + Type + "%");
        return vehiclesFromResultSet(getAllVehiclesStatement.executeQuery());
    }
    public ArrayList<Vehicle> getVehiclesForCustomer(int customerId) throws SQLException, ParseException {
        PreparedStatement getVehiclesStatement = query("SELECT * FROM `Vehicle` WHERE customer_id = ?");
        getVehiclesStatement.setInt(1, customerId);
        return vehiclesFromResultSet(getVehiclesStatement.executeQuery());
    }
    /**
     * returns an arraylist of all vehicles from a particular resultset
     */
    private static ArrayList<Vehicle> vehiclesFromResultSet(ResultSet rs) throws SQLException, ParseException {
        ArrayList<Vehicle> vehicles = new ArrayList<>();

        while (rs.next()) {
            // Get corresponding info
            String RegNo = rs.getString("RegistrationNo");
            int VehicleTemplateID = rs.getInt("VehicleTemplateId");
            VehicleTemplate template = Database.getInstance().getTemplate(VehicleTemplateID);
            String Colour = rs.getString("Colour");
            DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
            java.util.Date MotDate = df.parse(rs.getString("MOTRenewalDate"));
            Calendar MOTRenewalDate = Calendar.getInstance();
            MOTRenewalDate.setTime(MotDate);
            boolean Warranty = rs.getInt("Warranty") == 1;
            java.util.Date LastServDate = df.parse(rs.getString("LastServiceDate"));
            Calendar LastServiceDate = Calendar.getInstance();
            LastServiceDate.setTime(LastServDate);
            double CurrentMileage = rs.getDouble("CurrentMileage");
            int Customerid = rs.getInt("customer_id");
            Customer CustomerID = Database.getInstance().getCustomer(Customerid);
            int WarrantyID = rs.getInt("WarrantyID");
            Warranty warranty = Database.getInstance().getWarrantyforVehicle(WarrantyID);
            vehicles.add(new Vehicle(RegNo, template, Colour, MOTRenewalDate, Warranty,LastServiceDate, CurrentMileage,CustomerID,warranty));
        }
        return vehicles;
    }
    /**
     * Returns one vehicle object
     */
    public Vehicle getVehicle(String RegistrationNo) throws SQLException, ParseException {
        PreparedStatement getVehicleStatement

        = query("SELECT RegistrationNo,VehicleTemplateID,Colour,WarrantyID,MotRenewalDate,Warranty,LastServiceDate,CurrentMileage,customer_id FROM `Vehicle` WHERE `RegistrationNo` = ?;");

        getVehicleStatement.setString(1, RegistrationNo);
        ResultSet rs = getVehicleStatement.executeQuery() ;

        int VehicleTemplateID = rs.getInt("VehicleTemplateID");
        VehicleTemplate vt = Database.getInstance().getTemplate(VehicleTemplateID);
        String Colour = rs.getString("Colour");
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        java.util.Date MotDate = df.parse(rs.getString("MOTRenewalDate"));
        Calendar MOTRenewalDate = Calendar.getInstance();
        MOTRenewalDate.setTime(MotDate);
        boolean Warranty = rs.getInt("Warranty") == 1;
        java.util.Date LastServDate = df.parse(rs.getString("LastServiceDate"));
        Calendar LastServiceDate = Calendar.getInstance();
        LastServiceDate.setTime(LastServDate);
        double CurrentMileage = rs.getDouble("CurrentMileage");
        int Customerid = rs.getInt("customer_id");
        Customer CustomerID = Database.getInstance().getCustomer(Customerid);
        int WarrantyID = rs.getInt("WarrantyID");
        Warranty warranty = Database.getInstance().getWarrantyforVehicle(WarrantyID);
        return new Vehicle(RegistrationNo, vt, Colour, MOTRenewalDate, Warranty, LastServiceDate, CurrentMileage,CustomerID,warranty);

    }
    /**
     * Returns as an array list all the vehicle details
     */
    public ArrayList<Warranty> getAllWarrantyDetailsforVehicle() throws SQLException, ParseException {
        PreparedStatement getAllWarrantyStatement = query("SELECT * FROM `Warranty`");
        ArrayList<Warranty> vehicles = new ArrayList<>();

        ResultSet rs = getAllWarrantyStatement.executeQuery();

        while (rs.next()) {
            // Get corresponding info

            int WarrantyID = rs.getInt("WarrantyID");
            String NameofWarrantyCompany = rs.getString("NameofWarrantyCompany");
            String AddressLine1ofWarrantyCompany = rs.getString("AddressLine1ofWarrantyCompany");
            String town = rs.getString("Town");
            String City = rs.getString("City");
            String Postcode = rs.getString("Postcode");
            DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
            java.util.Date WarrExpiry = df.parse(rs.getString("DateofWarrantyExpiry"));
            Calendar DateofWarrantyExpiry = Calendar.getInstance();
            DateofWarrantyExpiry.setTime(WarrExpiry);
            vehicles.add(new Warranty(WarrantyID,NameofWarrantyCompany,AddressLine1ofWarrantyCompany,town,City,Postcode,DateofWarrantyExpiry));
        }
        return vehicles;
    }
    /**
     * Returns the warranty details for a particular vehicle
     */
    public Warranty getWarrantyforVehicle(int WarrantyID) throws SQLException, ParseException{
        PreparedStatement getWarrantyStatement
        = query("SELECT NameofWarrantyCompany, AddressLine1ofWarrantyCompany, Town, City, Postcode, DateofWarrantyExpiry FROM `Warranty` INNER JOIN 'Vehicle' ON Warranty.WarrantyID = Vehicle.WarrantyID WHERE Vehicle.WarrantyID = ?;");
        getWarrantyStatement.setInt(1,WarrantyID);
        ResultSet rs = getWarrantyStatement.executeQuery();

        //int WarrantyID = rs.getInt("WarrantyID");
        String NameofWarrantyCompany = rs.getString("NameofWarrantyCompany");
        String AddressLine1ofWarrantyCompany = rs.getString("AddressLine1ofWarrantyCompany");
        String Town = rs.getString("Town");
        String City = rs.getString("City");
        String Postcode = rs.getString("Postcode");
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        java.util.Date WarrExpiry = df.parse(rs.getString("DateofWarrantyExpiry"));
        Calendar DateofWarrantyExpiry = Calendar.getInstance();
        DateofWarrantyExpiry.setTime(WarrExpiry);
        return new Warranty(WarrantyID,NameofWarrantyCompany,AddressLine1ofWarrantyCompany,Town,City,Postcode,DateofWarrantyExpiry);
    }
    /**
     * Returns all the vehicle templates as an arraylist
     */
    public ArrayList<VehicleTemplate> getAllTemplate() throws SQLException {

        PreparedStatement getTemplateStatement
        = query("Select * from VehicleTemplate");

        // Execute query
        ArrayList<VehicleTemplate> vehicletemplates = new ArrayList<>();
        ResultSet rs = getTemplateStatement.executeQuery();
        while (rs.next()) {
            int id = rs.getInt("VehicleTemplateID");
            String Model = rs.getString("Model");
            String Make = rs.getString("Make");
            double EngineSize = rs.getDouble("EngineSize");
            String FuelType = rs.getString("FuelType");
            VehicleType vtype = VehicleType.CAR;
            if (rs.getString("VehicleType").equalsIgnoreCase("car")) {
                vtype = VehicleType.CAR;
            }
            else if (rs.getString("VehicleType").equalsIgnoreCase("truck")) {
                vtype = VehicleType.TRUCK;
            }
            if (rs.getString("VehicleType").equalsIgnoreCase("van")) {
                vtype = VehicleType.VAN;
            }
            vehicletemplates.add(new VehicleTemplate(id, Model, Make, EngineSize, FuelType,vtype));
        }
        return vehicletemplates;
    }
    public List<Vehicle> getAllVehiclesForCustomer(String CustomerName) throws SQLException, ParseException {
        ArrayList<Vehicle> vehicles = new ArrayList<>();
        PreparedStatement getTemplateStatement
        = query("Select RegistrationNo from Vehicle where Vehicle.customer_id = ?");
        getTemplateStatement.setString(1, CustomerName);
        ResultSet rs = getTemplateStatement.executeQuery();
        while (rs.next()) {
            String RegNo = rs.getString("RegistrationNo");

            Vehicle v = getVehicle(RegNo);
            vehicles.add(v);
        }
        return vehicles;
    }
    /**
     * returns the customerIDs for the combobox in the add and edit Vehicles prompts
     */

    public ArrayList<ExtCustomer> getAllCustomerIDsforVehicles() throws SQLException {
        PreparedStatement getAllCustomersStatement = query("SELECT id, full_name from Customer");
        //getAllCustomersStatement.setInt(1, CustomerID);
        ArrayList<ExtCustomer> customers = new ArrayList<>();
        ResultSet rs = getAllCustomersStatement.executeQuery();
        while (rs.next()) {
            // Get corresponding info
            int CustomerId = rs.getInt("id");
            String fullname = rs.getString("id");
            // Create customer object and add to list
            ExtCustomer customer = getCustomer1(CustomerId);
            customers.add(customer);
        }
        return customers;
    }
    /**
     * Gets a VehicleTemplate Object
     */
    public VehicleTemplate getTemplate(int vehicleTemplateID) throws SQLException {
        PreparedStatement getTemplateStatement
        = query("Select * from VehicleTemplate where VehicleTemplateID = ?");

        // Execute query
        getTemplateStatement.setInt(1, vehicleTemplateID);
        ResultSet rs = getTemplateStatement.executeQuery();
        int id = rs.getInt("VehicleTemplateID");
        String Model = rs.getString("Model");
        String Make = rs.getString("Make");
        double EngineSize = rs.getDouble("EngineSize");
        String FuelType = rs.getString("FuelType");
        VehicleType vtype = VehicleType.CAR;
            if (rs.getString("VehicleType").equalsIgnoreCase("car")) {
                vtype = VehicleType.CAR;
            }
            else if (rs.getString("VehicleType").equalsIgnoreCase("truck")) {
                vtype = VehicleType.TRUCK;
            }
            if (rs.getString("VehicleType").equalsIgnoreCase("van")) {
                vtype = VehicleType.VAN;
            }
        return new VehicleTemplate(id, Model, Make, EngineSize, FuelType,vtype);
    }
    /**
     * Adds the Vehicle Object
     */
    public void addVehicle(Vehicle vehicle) throws SQLException {
        //String temp = "";
        PreparedStatement addVehicleStatement = query("INSERT INTO `Vehicle` VALUES (?,?,?,?,?,?,?,?,?)");
        addVehicleStatement.setString(1, vehicle.getRegistrationNo());
        addVehicleStatement.setInt(2, vehicle.getTemplate().getId());
        addVehicleStatement.setString(3, vehicle.getColour());
        addVehicleStatement.setString(4, DateHelperVeh.toString(vehicle.getMOTRenewalDate()));
        addVehicleStatement.setBoolean(5, vehicle.isWarrantyActive());
        addVehicleStatement.setString(6, DateHelperVeh.toString(vehicle.getLastServiceDate()));
        addVehicleStatement.setDouble(7, vehicle.getCurrentMileage());
        addVehicleStatement.setString(8, vehicle.getCustomer().toString());
        /*if(vehicle.getWarranty().getWarranty() != 0){
            temp = vehicle.getWarranty().toString();
        }
        addVehicleStatement.setString(9, temp);*/
        addVehicleStatement.setString(9, vehicle.getWarranty().toString());
        addVehicleStatement.execute();
    }
    public void addVehicleTemp(VehicleTemplate vt) throws SQLException {
        PreparedStatement addVehicleTempStatement = query("INSERT INTO `VehicleTemplate` VALUES (?,?,?,?,?,?)");
        addVehicleTempStatement.setInt(1, vt.getId());
        addVehicleTempStatement.setString(2, vt.getMake());
        addVehicleTempStatement.setString(3, vt.getModel());
        addVehicleTempStatement.setDouble(4, vt.getEngineSize());
        addVehicleTempStatement.setString(5, vt.getFuelType());
        addVehicleTempStatement.setString(6, vt.getType().toString());
        
        addVehicleTempStatement.execute();
    }
    public void addWarrantyComp(Warranty w) throws SQLException {
        PreparedStatement addVehicleTempStatement = query("INSERT INTO `Warranty` VALUES (?,?,?,?,?,?,?)");
        addVehicleTempStatement.setInt(1, w.getId());
        addVehicleTempStatement.setString(2, w.getCompanyName());
        addVehicleTempStatement.setString(3, w.getAddressLine1());
        addVehicleTempStatement.setString(4, w.getTown());
        addVehicleTempStatement.setString(5, w.getCity());
        addVehicleTempStatement.setString(6, w.getPostcode());
        addVehicleTempStatement.setString(7, DateHelperVeh.toString(w.getExpiryDate()));
        
        addVehicleTempStatement.execute();
    }
    /**
     * Edits the Vehicle Object
     */
    public void editVehicle(Vehicle vehicle) throws SQLException {
        PreparedStatement editVehicleStatement = query("UPDATE `Vehicle` SET VehicleTemplateID = ?, Colour = ?, MOTRenewalDate = ?, Warranty = ?, LastServiceDate = ?, CurrentMileage = ?, customer_id = ?,WarrantyId = ? WHERE RegistrationNo = ? ");
        //editVehicleStatement.setString(1, vehicle.getRegistrationNo());
        editVehicleStatement.setInt(1, vehicle.getTemplate().getId());
        editVehicleStatement.setString(2, vehicle.getColour());
        editVehicleStatement.setString(3, DateHelperVeh.toString(vehicle.getMOTRenewalDate()));
        editVehicleStatement.setBoolean(4, vehicle.isWarrantyActive());
        editVehicleStatement.setString(5, DateHelperVeh.toString(vehicle.getLastServiceDate()));
        editVehicleStatement.setDouble(6, vehicle.getCurrentMileage());
        editVehicleStatement.setString(7, vehicle.getCustomer().toString());
        editVehicleStatement.setString(8, vehicle.getWarranty().toString());
        editVehicleStatement.setString(9, vehicle.getRegistrationNo());
        int k = editVehicleStatement.executeUpdate();

        updateInvoiceForVehicleChange(vehicle.getRegistrationNo());
    }
    public void editWarranty(Warranty w) throws SQLException {
        PreparedStatement editWarrantyStatement = query("UPDATE `Warranty` SET NameofWarrantyCompany = ?, AddressLine1ofWarrantyCompany = ?, Town = ?, City = ?, Postcode = ?, DateofWarrantyExpiry = ? WHERE WarrantyID = ? ");
        //editVehicleStatement.setString(1, vehicle.getRegistrationNo());
        editWarrantyStatement.setString(1, w.getCompanyName());
        editWarrantyStatement.setString(2, w.getAddressLine1());
        editWarrantyStatement.setString(3, w.getTown());
        editWarrantyStatement.setString(4, w.getCity());
        editWarrantyStatement.setString(5, w.getPostcode());
        editWarrantyStatement.setString(6, DateHelperVeh.toString(w.getExpiryDate()));
        editWarrantyStatement.setInt(7, w.getId());
        
        editWarrantyStatement.executeUpdate();
        System.out.println(w);
    }
    /**
     * Removes the Vehicle Object
     */
    public void removeVehicle(Vehicle vehicle) throws SQLException, ParseException {
        removeVehicle(vehicle.getRegistrationNo());
    }
    /**
     * Removes the Vehicle with the specified VehicleRegiNo
     */
    public void removeVehicle(String RegistrationNo) throws SQLException, ParseException {
        
        List<Booking> allBookings = getAllBookingsForVehicleRegNum(RegistrationNo, false);
        for (Booking b: allBookings){
            removeBooking(b);
        }
        PreparedStatement removeVehicleStatement = query("DELETE FROM `Vehicle` WHERE RegistrationNo = ?");
        removeVehicleStatement.setString(1, RegistrationNo);
        removeVehicleStatement.executeUpdate();
        //removebookingsforVehicle(RegistrationNo);
        
        //removeCustomerforVehicle(RegistrationNo);
    }
    /*public void removebookingsforVehicle(String RegistrationNo) throws SQLException, ParseException {
       

        List<Booking> allBookings = getAllBookingsForVehicleRegNum(RegistrationNo, false);
        for (Booking b: allBookings){
            removeBooking(b);
        }
        
    }*/
    public void removeVehicleTemplate(int VehTempID) throws SQLException {
        PreparedStatement removeVehicleTempStatement = query("DELETE FROM `VehicleTemplate` WHERE VehicleTemplateID = ?");
        removeVehicleTempStatement.setInt(1, VehTempID);
        removeVehicleTempStatement.executeUpdate();
    }
    public void removeWarranty(int WarrantyID) throws SQLException {
        PreparedStatement removeVehicleTempStatement = query("DELETE FROM `Warranty` WHERE WarrantyID = ?");
        removeVehicleTempStatement.setInt(1, WarrantyID);
        removeVehicleTempStatement.executeUpdate();
    }
    /**
     * Checks if vehicle with RegNo exists elsewhere
     */
    public boolean VehicleExists(String RegistrationNo) throws SQLException {
        PreparedStatement VehicleExistsStatement
        = query("SELECT COUNT(RegistrationNo) AS total FROM `Vehicle` WHERE RegistrationNo = ?");
        VehicleExistsStatement.setString(1, RegistrationNo);
        return VehicleExistsStatement.executeQuery().getInt("total") > 0;
    }

    public void addToBookingStockQuantity(int id, int amount) throws SQLException {
        PreparedStatement getQuantity = query("SELECT quantity FROM `Parts` WHERE id=?");
        getQuantity.setInt(1, id);
        int amtInStock = getQuantity.executeQuery().getInt("quantity");

        PreparedStatement updateQuantity = query("UPDATE `Parts` SET quantity = ? WHERE id = ?");
        updateQuantity.setInt(1, amount + amtInStock);
        updateQuantity.setInt(2, id);
        updateQuantity.executeUpdate();
    }


    public void editBooking(Booking booking) throws SQLException {
        // Perform booking change
        PreparedStatement statement = query("UPDATE `booking` SET booking_type = ?, mechanic_id = ?, start_date = ?,"
                + " end_date = ?, mechanic_hrs_spent = ?, customer_id = ?,"
                + " vehicle_id = ?, vehicle_mileage = ? WHERE booking_id = ?");
        int bookingType = booking instanceof DiagnosisRepairBooking ? 1 : 0;
        statement.setInt(1, bookingType);
        statement.setInt(2, booking.getMechanic().getId());
        statement.setString(3, DateHelper.toString(booking.getBookingStartDate()));
        statement.setString(4, DateHelper.toString(booking.getBookingEndDate()));
        statement.setString(5, DateHelper.toString(booking.getMechanicTimeSpent()));
        statement.setInt(6, booking.getCustomer().getId());
        statement.setString(7, booking.getVehicle().getRegistrationNo());

        // Set vehicle mileage
        if (booking instanceof DiagnosisRepairBooking) {
            DiagnosisRepairBooking b = (DiagnosisRepairBooking)booking;
            statement.setInt(8, b.getVehicleMileage());
        } else {
            statement.setInt(8, -1);
        }
        statement.setInt(9, booking.getId());
        statement.executeUpdate();

        // Perform invoice change
        updateInvoice(booking.getInvoice());
    }

    public void updateInvoice(Invoice invoice) throws SQLException {
        PreparedStatement statement = query("UPDATE `invoice` SET booking_base_cost = ?, amt_paid = ?, "
                + "payment_complete = ?, vehicle_warranty_active = ?, spc_total_cost = ?, spc_amt_due = ?, "
                + "booking_total_cost = ?, booking_amt_due = ? WHERE invoice_id = ?");
        statement.setDouble(1, invoice.getBookingBaseCost());
        statement.setDouble(2, invoice.getAmtPaid());
        statement.setInt(3, invoice.isSettled() ? 1 : 0);
        statement.setInt(4, invoice.isVehicleWarrantyActive() ? 1 : 0);
        statement.setDouble(5, invoice.getSpcTotalCost());
        statement.setDouble(6, invoice.getSpcAmountDue());
        statement.setDouble(7, invoice.getBookingTotalCost());
        statement.setDouble(8, invoice.getBookingAmountDue());
        statement.setInt(9, invoice.getId());
        statement.executeUpdate();
    }

    public Collection<Invoice> getAllBills() throws SQLException {
        PreparedStatement getAllBillsStatement = query("SELECT * FROM invoice"); 
        ResultSet rs = getAllBillsStatement.executeQuery();
        Collection<Invoice> bills = new ArrayList<>();

        while (rs.next()) {
           bills.add(invoiceFromResultSet(rs));
        }
        return bills;
    } 
    
     public List<Booking> getAllBillsForCustomer() throws SQLException, ParseException {
       PreparedStatement getAllBillsStatement = query("SELECT * FROM booking NATURAL JOIN invoice INNER JOIN Customer ON booking.customer_id = Customer.id");
        ResultSet rs = getAllBillsStatement.executeQuery();
        return bookingsFromResultSet(rs);
    } 

    public List<Booking> getAllDiagRepBookingsWithNoSPCBooking() throws SQLException, ParseException {
        List<Booking> allDiagRepBookingsWithNoSpcBookings = new ArrayList();       
        List<Booking> allBookings = getAllBookings();
        for (Booking booking : allBookings) { // NOTE this is probably slow, re-write this as an sql query for improved speed
            SPCBooking spcBooking = getSPCBookingForBooking(booking);
            if(spcBooking==null&&booking instanceof DiagnosisRepairBooking)
            {
                allDiagRepBookingsWithNoSpcBookings.add(booking);
            }
        }
        return allDiagRepBookingsWithNoSpcBookings;
    }

    private void updateInvoiceForVehicleChange(String regNum) throws SQLException {
        // Get bookings affected
        logger.debug("Updating invoices due to vehicle change (id={})", regNum);
        PreparedStatement statement = query("SELECT * FROM booking NATURAL JOIN invoice WHERE vehicle_id = ? AND end_date >= ?");
        statement.setString(1, regNum);
        statement.setString(2, DateHelper.toString(Calendar.getInstance()));

        // Update invoices
        updateInvoices(statement.executeQuery());
    }

    private void updateInvoiceForPartWarrantyChange(int partId) throws SQLException {
        // Get bookings affected
        logger.debug("Updating invoices due to part warranty change (id={})", partId);
        PreparedStatement statement = query("SELECT * FROM booking NATURAL JOIN invoice "
                + "WHERE booking_id IN (SELECT booking_id AS bid FROM PartsInstalled WHERE booking_id = bid AND part_id = ? AND end_date >= ?)");
        statement.setInt(1, partId);
        statement.setString(2, DateHelper.toString(Calendar.getInstance()));

        // Update invoices
        updateInvoices(statement.executeQuery());
    }

    private void updateInvoiceForStockPartChange(int partId) throws SQLException {
        // Get bookings affected
        logger.debug("Updating invoices due to part change (id={})", partId);
        PreparedStatement statement = query("SELECT * FROM booking NATURAL JOIN invoice "
                + "WHERE booking_id IN (SELECT booking_id AS bid FROM PartsInstalled WHERE booking_id = bid AND part_id = ?)");
        statement.setInt(1, partId);

        // Update invoices
        updateInvoices(statement.executeQuery());
    }

    private void updateInvoiceForMechanicChange(int mechanicId) throws SQLException {
        // Get bookings affected
        logger.debug("Updating invoices due to mechanic change (id={})", mechanicId);
        PreparedStatement statement = query("SELECT * FROM booking NATURAL JOIN invoice WHERE mechanic_id = ?");
        statement.setInt(1, mechanicId);

        // Update invoices
        updateInvoices(statement.executeQuery());
    }

    private void updateInvoices(ResultSet rs) throws SQLException {
        // Get bookings affected
        List<Booking> bookings = new ArrayList<>();

        try {
            bookings = bookingsFromResultSet(rs);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        logger.debug("Bookings identified: {}", Booking.listIds(bookings));

        // Update invoices
        for (Booking booking : bookings) {
            int bookingId = booking.getId();
            Invoice invoice = booking.getInvoice();

            // Get booking costs
            double basicCosts = invoice.getBookingBaseCost() + booking.mechanicCost();
            double totalCost = MathHelper.round(getTotalCostWithWarranty(bookingId) + basicCosts);
            double amtDue = MathHelper.round(getTotalCostWithoutWarranty(bookingId) + basicCosts);

            logger.debug("BID: {} | total costs: {}* vs {} | amt dues: {}* vs {} | * indicates potentially new values",
                    bookingId, totalCost, invoice.getBookingTotalCost(), amtDue, invoice.getBookingAmountDue());

            if (invoice.getBookingTotalCost() != totalCost || invoice.getBookingAmountDue() != amtDue
                    || invoice.isOriginalSettled() != invoice.isSettled()
                    || !invoice.isSettled() && booking.getVehicle().isWarrantyActive()) {
                logger.debug("Proceeding with update for booking with id {}", bookingId);

                // Update invoice
                invoice.setBookingTotalCost(totalCost);
                invoice.setBookingAmountDue(amtDue);

                if (booking.getVehicle().isWarrantyActive()
                        && booking.getBookingStartDate().compareTo(Calendar.getInstance()) >= 0
                        && booking.getBookingEndDate().compareTo(booking.getVehicle().getWarranty().getExpiryDate()) <= 0) {
                    invoice.activateVehicleWarranty();
                }
                invoice.setSettled(invoice.getAmtPaid() >= invoice.getTotalAmountDue());

                // Submit the update, if the values have changed
                updateInvoice(invoice);
            }
        }
    }

    /**
     * This is a wrapper class for the customer class to add an override method in a way that doesn't affect his module
     * XXX this is bad and needs to be addressed, if we have spare time.
     */
    public class ExtCustomer extends Customer {
        public ExtCustomer(int id, String fullname, String phone, String email,String city, String streetaddress, String postcode, CustomerType type) {
            super(id, fullname, phone, email, city,streetaddress, postcode, type);
        }

        @Override
        public String toString() {
            return id + " ";
        }
    }
}
