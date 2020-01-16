package com.smartit.beunique.entity.magazine;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by android on 28/2/19.
 */

public class EOMagazineComment implements Serializable {

    @SerializedName("customer_name")
    @Expose
    private String customerName;
    @SerializedName("content")
    @Expose
    private String content;
    @SerializedName("posted_on")
    @Expose
    private String postedOn;

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPostedOn() {
        return postedOn;
    }

    public void setPostedOn(String postedOn) {
        this.postedOn = postedOn;
    }

}
