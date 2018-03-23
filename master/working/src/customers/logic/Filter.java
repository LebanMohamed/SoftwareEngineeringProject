/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package customers.logic;

/**
 *
 * @author cawaala
 */
public class Filter {

    private String filName;
    private String filValue;

    public Filter(String filname, String filValue) {
        this.filName = filname;
        this.filValue = filValue;
    }

    public void setFilValue(String filValue) {
        this.filValue = filValue;
    }

    public String getFilValue() {
        return filValue;
    }

    public String getFilName() {
        return filName;
    }

    public void setFilName(String filName) {
        this.filName = filName;
    }

}
