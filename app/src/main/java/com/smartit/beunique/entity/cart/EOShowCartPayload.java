package com.smartit.beunique.entity.cart;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by android on 26/3/19.
 */

public class EOShowCartPayload implements Serializable {

    @SerializedName("total_cart")
    @Expose
    private Integer totalCart;
    @SerializedName("result")
    @Expose
    private List<EOShowCartResult> result = null;

    public Integer getTotalCart() {
        return totalCart;
    }

    public void setTotalCart(Integer totalCart) {
        this.totalCart = totalCart;
    }

    public List <EOShowCartResult> getResult() {
        return result;
    }

    public void setResult(List <EOShowCartResult> result) {
        this.result = result;
    }

}
