package com.smartit.beunique.entity.allproducts;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by android on 8/3/19.
 */

public class EOLikeProductCount implements Serializable {

    @SerializedName("count")
    @Expose
    private String count;
    @SerializedName("id_product")
    @Expose
    private String idProduct;

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getIdProduct() {
        return idProduct;
    }

    public void setIdProduct(String idProduct) {
        this.idProduct = idProduct;
    }

}
