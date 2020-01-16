package com.smartit.beunique.entity.language;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by android on 1/2/19.
 */

public class EOLanguage implements Serializable {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("iso_code")
    @Expose
    private String isoCode;
    @SerializedName("language_code")
    @Expose
    private String languageCode;
    @SerializedName("active")
    @Expose
    private String active;
    @SerializedName("is_rtl")
    @Expose
    private String isRtl;
    @SerializedName("date_format_lite")
    @Expose
    private String dateFormatLite;
    @SerializedName("date_format_full")
    @Expose
    private String dateFormatFull;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIsoCode() {
        return isoCode;
    }

    public void setIsoCode(String isoCode) {
        this.isoCode = isoCode;
    }

    public String getLanguageCode() {
        return languageCode;
    }

    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
    }

    public String getActive() {
        return active;
    }

    public void setActive(String active) {
        this.active = active;
    }

    public String getIsRtl() {
        return isRtl;
    }

    public void setIsRtl(String isRtl) {
        this.isRtl = isRtl;
    }

    public String getDateFormatLite() {
        return dateFormatLite;
    }

    public void setDateFormatLite(String dateFormatLite) {
        this.dateFormatLite = dateFormatLite;
    }

    public String getDateFormatFull() {
        return dateFormatFull;
    }

    public void setDateFormatFull(String dateFormatFull) {
        this.dateFormatFull = dateFormatFull;
    }
}

