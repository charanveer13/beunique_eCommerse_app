package com.smartit.beunique.entity.pointsSystem;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by android on 18/3/19.
 */

public class EOPointsSystem implements Serializable {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("id_order")
    @Expose
    private String idOrder;
    @SerializedName("id_loyalty_state")
    @Expose
    private String idLoyaltyState;
    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("points")
    @Expose
    private String points;
    @SerializedName("points_status")
    @Expose
    private String pointsStatus;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdOrder() {
        return idOrder;
    }

    public void setIdOrder(String idOrder) {
        this.idOrder = idOrder;
    }

    public String getIdLoyaltyState() {
        return idLoyaltyState;
    }

    public void setIdLoyaltyState(String idLoyaltyState) {
        this.idLoyaltyState = idLoyaltyState;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPoints() {
        return points;
    }

    public void setPoints(String points) {
        this.points = points;
    }

    public String getPointsStatus() {
        return pointsStatus;
    }

    public void setPointsStatus(String pointsStatus) {
        this.pointsStatus = pointsStatus;
    }

}
