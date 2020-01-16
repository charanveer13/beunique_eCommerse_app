package com.smartit.beunique.entity.currency;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by android on 1/2/19.
 */

public class EOCurrency implements Serializable {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("iso_code")
    @Expose
    private String isoCode;
    @SerializedName("iso_code_num")
    @Expose
    private String isoCodeNum;
    @SerializedName("blank")
    @Expose
    private String blank;
    @SerializedName("sign")
    @Expose
    private String sign;
    @SerializedName("format")
    @Expose
    private String format;
    @SerializedName("decimals")
    @Expose
    private String decimals;
    @SerializedName("conversion_rate")
    @Expose
    private String conversionRate;
    @SerializedName("deleted")
    @Expose
    private String deleted;
    @SerializedName("active")
    @Expose
    private String active;

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

    public String getIsoCodeNum() {
        return isoCodeNum;
    }

    public void setIsoCodeNum(String isoCodeNum) {
        this.isoCodeNum = isoCodeNum;
    }

    public String getBlank() {
        return blank;
    }

    public void setBlank(String blank) {
        this.blank = blank;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getDecimals() {
        return decimals;
    }

    public void setDecimals(String decimals) {
        this.decimals = decimals;
    }

    public String getConversionRate() {
        return conversionRate;
    }

    public void setConversionRate(String conversionRate) {
        this.conversionRate = conversionRate;
    }

    public String getDeleted() {
        return deleted;
    }

    public void setDeleted(String deleted) {
        this.deleted = deleted;
    }

    public String getActive() {
        return active;
    }

    public void setActive(String active) {
        this.active = active;
    }

}
