package com.smartit.beunique.entity.country;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by android on 9/3/19.
 */

public class EOCountryList implements Serializable {

    @SerializedName("countries")
    @Expose
    private List <EOCountry> countries = null;

    public List <EOCountry> getCountries() {
        return countries;
    }

    public void setCountries(List <EOCountry> countries) {
        this.countries = countries;
    }


}
