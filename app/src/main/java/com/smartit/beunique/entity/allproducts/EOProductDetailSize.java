package com.smartit.beunique.entity.allproducts;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by android on 9/3/19.
 */

public class EOProductDetailSize implements Serializable {

    @SerializedName("id_lang")
    @Expose
    private String idLang;
    @SerializedName("id_attribute")
    @Expose
    private String idAttribute;
    @SerializedName("name")
    @Expose
    private String name;

    public String getIdLang() {
        return idLang;
    }

    public void setIdLang(String idLang) {
        this.idLang = idLang;
    }

    public String getIdAttribute() {
        return idAttribute;
    }

    public void setIdAttribute(String idAttribute) {
        this.idAttribute = idAttribute;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


}
