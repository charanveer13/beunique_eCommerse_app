package com.smartit.beunique.entity.manufacturers;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by android on 12/2/19.
 */

public class EOManufacturersList implements Serializable {

    @SerializedName("manufacturers")
    @Expose
    private List <EOManufacturers> manufacturers = null;

    public List <EOManufacturers> getManufacturers() {
        return manufacturers;
    }

    public void setManufacturers(List <EOManufacturers> manufacturers) {
        this.manufacturers = manufacturers;
    }

}