package com.smartit.beunique.entity.dashboardCategory;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by android on 22/2/19.
 */

public class EOPayload implements Serializable {

    @SerializedName("count")
    @Expose
    private Integer count;

    @SerializedName("product")
    @Expose
    private List <String> product = null;

    @SerializedName("total_no_of_pages")
    @Expose
    private Integer total_no_of_pages;

    public Integer getTotal_no_of_pages() {
        return total_no_of_pages;
    }

    public void setTotal_no_of_pages(Integer total_no_of_pages) {
        this.total_no_of_pages = total_no_of_pages;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public List <String> getProduct() {
        return product;
    }

    public void setProduct(List <String> product) {
        this.product = product;
    }

}
