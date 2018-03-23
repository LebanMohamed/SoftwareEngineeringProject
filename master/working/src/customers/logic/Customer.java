package customers.logic;

/**
 *
 * @author cawaala
 */
public class Customer {

    public int id;
    private String fullname;
    private String phone;
    private String email;
    private String streetaddress;
    private String city;
    public CustomerType type;
    private String postcode;

    public enum CustomerType {
        Private,
        Business;
    }

    public Customer(int id, String fullname, String phone, String email,
            String city, String streetaddress, String postcode, CustomerType type) {
        this.id = id;
        this.fullname = fullname;
        this.phone = phone;
        this.email = email;
        this.city = city;
        this.streetaddress = streetaddress;
        this.type = type;
        this.postcode = postcode;
    }

    public String getCity() {
        return city;
    }

    public int getId() {
        return this.id;
    }

    public String getFullName() {
        return this.fullname;
    }

    public void setFullName(String fullname) {
        this.fullname = fullname;
    }

    public String getPhone() {
        return this.phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getStreetAddress() {
        return this.streetaddress;
    }

    public void setStreetAddress(String streetaddress) {
        this.streetaddress = streetaddress;
    }

    public String getEmail() {
        return this.email;
    }

    public void setType(CustomerType type) {
        this.type = type;
    }

    public CustomerType getType() {
        return type;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPostcode() {
        return this.postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

}
