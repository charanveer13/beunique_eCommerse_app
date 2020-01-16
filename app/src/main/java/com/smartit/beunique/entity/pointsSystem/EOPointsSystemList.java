package com.smartit.beunique.entity.pointsSystem;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by android on 18/3/19.
 */

public class EOPointsSystemList implements Serializable {

    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("payload")
    @Expose
    private List <EOPointsSystem> payload = null;

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

    public List <EOPointsSystem> getPayload() {
        return payload;
    }

    public void setPayload(List <EOPointsSystem> payload) {
        this.payload = payload;
    }

}
