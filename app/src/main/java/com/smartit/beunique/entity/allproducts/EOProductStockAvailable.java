package com.smartit.beunique.entity.allproducts;

/**
 * Created by android on 18/1/19.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class EOProductStockAvailable implements Serializable {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("id_product_attribute")
    @Expose
    private String idProductAttribute;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdProductAttribute() {
        return idProductAttribute;
    }

    public void setIdProductAttribute(String idProductAttribute) {
        this.idProductAttribute = idProductAttribute;
    }

}
