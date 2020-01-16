package com.smartit.beunique.entity.allproducts;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by android on 15/2/19.
 */

public class EOProductFeature implements Serializable {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("id_feature_value")
    @Expose
    private String idFeatureValue;
    @SerializedName("id_product")
    @Expose
    private String idProduct;
    @SerializedName("id_feature")
    @Expose
    private String idFeature;
    @SerializedName("id_lang")
    @Expose
    private String idLang;
    @SerializedName("name")
    @Expose
    private String name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdFeatureValue() {
        return idFeatureValue;
    }

    public void setIdFeatureValue(String idFeatureValue) {
        this.idFeatureValue = idFeatureValue;
    }

    public String getIdProduct() {
        return idProduct;
    }

    public void setIdProduct(String idProduct) {
        this.idProduct = idProduct;
    }

    public String getIdFeature() {
        return idFeature;
    }

    public void setIdFeature(String idFeature) {
        this.idFeature = idFeature;
    }

    public String getIdLang() {
        return idLang;
    }

    public void setIdLang(String idLang) {
        this.idLang = idLang;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
