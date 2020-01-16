package com.smartit.beunique.entity.dashboardCategory;

import com.smartit.beunique.entity.allproducts.EOTag;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class EOCategoryAssociations implements Serializable {

    //TODO due to make a new object used previous objects for id object

    @Expose
    @SerializedName("products")
    private List<EOTag> products;

    public List<EOTag> getProducts() {
        return products;
    }

    public void setProducts(List<EOTag> products) {
        this.products = products;
    }
}
