package com.smartit.beunique.entity.account;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by android on 1/3/19.
 */

public class EOLoginCustomers implements Serializable {

    @SerializedName("customer")
    @Expose
    private EOAccountCustomer customer;

    public EOAccountCustomer getCustomer() {
        return customer;
    }

    public void setCustomer(EOAccountCustomer customer) {
        this.customer = customer;
    }

}
