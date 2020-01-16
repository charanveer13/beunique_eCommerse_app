package com.smartit.beunique.entity.account;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by android on 27/3/19.
 */

public class EOUpdateProfileObject implements Serializable {

    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("payload")
    @Expose
    private EOUpdateProfilePayload payload;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public EOUpdateProfilePayload getPayload() {
        return payload;
    }

    public void setPayload(EOUpdateProfilePayload payload) {
        this.payload = payload;
    }

}
