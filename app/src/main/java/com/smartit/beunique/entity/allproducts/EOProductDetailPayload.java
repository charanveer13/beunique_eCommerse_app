package com.smartit.beunique.entity.allproducts;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by android on 8/3/19.
 */

public class EOProductDetailPayload implements Serializable {

    @SerializedName("like_product_count")
    @Expose
    private List <EOLikeProductCount> likeProductCount = null;
    @SerializedName("wishlist")
    @Expose
    private List <EOProductDetailWishlist> wishlist = null;
    @SerializedName("videos")
    @Expose
    private List <EOProductVideo> videos = null;
    @SerializedName("comments")
    @Expose
    private List <EOProductComment> comments = null;
    @SerializedName("features")
    @Expose
    private List <EOProductFeature> features = null;
    @SerializedName("feature_values")
    @Expose
    private List <EOProductFeature> featureValues = null;
    @SerializedName("product_option_values")
    @Expose
    private List <EOProductDetailSize> product_option_values = null;

    public List <EOProductDetailSize> getProduct_option_values() {
        return product_option_values;
    }

    public void setProduct_option_values(List <EOProductDetailSize> product_option_values) {
        this.product_option_values = product_option_values;
    }

    public List <EOProductFeature> getFeatures() {
        return features;
    }

    public void setFeatures(List <EOProductFeature> features) {
        this.features = features;
    }

    public List <EOProductFeature> getFeatureValues() {
        return featureValues;
    }

    public void setFeatureValues(List <EOProductFeature> featureValues) {
        this.featureValues = featureValues;
    }

    public List <EOLikeProductCount> getLikeProductCount() {
        return likeProductCount;
    }

    public void setLikeProductCount(List <EOLikeProductCount> likeProductCount) {
        this.likeProductCount = likeProductCount;
    }

    public List <EOProductDetailWishlist> getWishlist() {
        return wishlist;
    }

    public void setWishlist(List <EOProductDetailWishlist> wishlist) {
        this.wishlist = wishlist;
    }

    public List <EOProductVideo> getVideos() {
        return videos;
    }

    public void setVideos(List <EOProductVideo> videos) {
        this.videos = videos;
    }

    public List <EOProductComment> getComments() {
        return comments;
    }

    public void setComments(List <EOProductComment> comments) {
        this.comments = comments;
    }

}
