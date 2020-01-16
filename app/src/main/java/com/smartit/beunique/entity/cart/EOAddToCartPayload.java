package com.smartit.beunique.entity.cart;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by android on 26/3/19.
 */

public class EOAddToCartPayload implements Serializable {

    @SerializedName("cart")
    @Expose
    private EOAddToCart cart;

    public EOAddToCart getCart() {
        return cart;
    }

    public void setCart(EOAddToCart cart) {
        this.cart = cart;
    }

}
