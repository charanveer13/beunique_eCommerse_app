package com.smartit.beunique.entity.account;

/**
 * Created by android on 15/1/19.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class EOAccountPayload implements Serializable {

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
