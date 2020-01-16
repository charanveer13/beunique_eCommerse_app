package com.smartit.beunique.entity.allproducts;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by android on 15/2/19.
 */

public class EOBrandProducts implements Serializable{

    @SerializedName("products")
    @Expose
    private List<EOAllProductData> products = null;

    public List<EOAllProductData> getProducts() {
        return products;
    }

    public void setProducts(List<EOAllProductData> products) {
        this.products = products;
    }

}
