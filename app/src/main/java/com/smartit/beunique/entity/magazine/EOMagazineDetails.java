package com.smartit.beunique.entity.magazine;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by android on 28/2/19.
 */

public class EOMagazineDetails implements Serializable {

    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("payload")
    @Expose
    private EOMagazineDetailPayload payload;

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

    public EOMagazineDetailPayload getPayload() {
        return payload;
    }

    public void setPayload(EOMagazineDetailPayload payload) {
        this.payload = payload;
    }

}
