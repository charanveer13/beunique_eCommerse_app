package com.smartit.beunique.entity.magazine;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by android on 26/2/19.
 */

public class EOMagazineList implements Serializable {

    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("payload")
    @Expose
    private EOMagazinePayload payload = null;

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

    public EOMagazinePayload getPayload() {
        return payload;
    }

    public void setPayload(EOMagazinePayload payload) {
        this.payload = payload;
    }

}
