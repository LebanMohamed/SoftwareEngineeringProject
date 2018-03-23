/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package specialist.logic;

/**
 *
 * @author Nikita Mirolyubov ec15143
 */
public final class SPC {

    private int id;
    private String name;
    private String phone;
    private String address;
    private String city;
    private String postcode;
    private String email;

    public SPC(int id, String name, String phone, String email, String city, String address, String postcode) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.city = city;
        this.address = address;
        this.postcode = postcode;

    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {

        return this.name;
    }

    public String getPhone() {
        return this.phone;
    }

    public String getAddress() {
        return this.address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail() {
        return this.email;
    }

    public String getCity() {
        return this.city;
    }

    public String getPostcode() {
        return this.postcode;
    }

}
