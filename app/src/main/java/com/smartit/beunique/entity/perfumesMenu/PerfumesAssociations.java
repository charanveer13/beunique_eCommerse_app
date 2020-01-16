package com.smartit.beunique.entity.perfumesMenu;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by android on 16/2/19.
 */

public class PerfumesAssociations implements Serializable {

    @SerializedName("products")
    @Expose
    private List<PerfumesProduct> products = null;

    public List<PerfumesProduct> getProducts() {
        return products;
    }

    public void setProducts(List<PerfumesProduct> products) {
        this.products = products;
    }

}
