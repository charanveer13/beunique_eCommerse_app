package com.smartit.beunique.entity.allproducts;

/**
 * Created by android on 18/1/19.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class EOProductAssociations implements Serializable {

    @SerializedName("categories")
    @Expose
    private List <EOProductCategory> categories = null;
    @SerializedName("images")
    @Expose
    private List <EOProductImage> images = null;
    @SerializedName("stock_availables")
    @Expose
    private List <EOProductStockAvailable> stockAvailables = null;
    @SerializedName("product_features")
    @Expose
    private List <EOProductFeature> productFeatures = null;

    @SerializedName("tags")
    @Expose
    private List <EOTag> tags = null;

    public List <EOTag> getTags() {
        return tags;
    }

    public void setTags(List <EOTag> tags) {
        this.tags = tags;
    }

    public List <EOProductFeature> getProductFeatures() {
        return productFeatures;
    }

    public void setProductFeatures(List <EOProductFeature> productFeatures) {
        this.productFeatures = productFeatures;
    }

    public List <EOProductCategory> getCategories() {
        return categories;
    }

    public void setCategories(List <EOProductCategory> categories) {
        this.categories = categories;
    }

    public List <EOProductImage> getImages() {
        return images;
    }

    public void setImages(List <EOProductImage> images) {
        this.images = images;
    }

    public List <EOProductStockAvailable> getStockAvailables() {
        return stockAvailables;
    }

    public void setStockAvailables(List <EOProductStockAvailable> stockAvailables) {
        this.stockAvailables = stockAvailables;
    }

}
