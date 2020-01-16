package com.smartit.beunique.entity.dashboardCategory;

import com.smartit.beunique.entity.allproducts.EOAllProductData;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by android on 24/1/19.
 */

public class EOCategoryProducts implements Serializable {

    @SerializedName("product")
    @Expose
    private EOAllProductData product;

    public EOAllProductData getProduct() {
        return product;
    }

    public void setProduct(EOAllProductData product) {
        this.product = product;
    }
}
