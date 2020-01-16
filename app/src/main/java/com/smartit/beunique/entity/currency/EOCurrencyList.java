package com.smartit.beunique.entity.currency;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by android on 1/2/19.
 */

public class EOCurrencyList implements Serializable {

    @SerializedName("currencies")
    @Expose
    private List <EOCurrency> currencies = null;

    public List <EOCurrency> getCurrencies() {
        return currencies;
    }

    public void setCurrencies(List <EOCurrency> currencies) {
        this.currencies = currencies;
    }

}
