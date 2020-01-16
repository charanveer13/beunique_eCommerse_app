package com.smartit.beunique.entity.account;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by android on 27/3/19.
 */

public class EOUpdateProfilePayload implements Serializable{

    @SerializedName("customer")
    @Expose
    private EOUpdateProfileCustomer customer;

    public EOUpdateProfileCustomer getCustomer() {
        return customer;
    }

    public void setCustomer(EOUpdateProfileCustomer customer) {
        this.customer = customer;
    }

}
