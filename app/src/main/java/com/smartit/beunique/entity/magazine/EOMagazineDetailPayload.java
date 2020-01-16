package com.smartit.beunique.entity.magazine;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by android on 28/2/19.
 */

public class EOMagazineDetailPayload implements Serializable {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("accept_comment")
    @Expose
    private String acceptComment;
    @SerializedName("total_comments")
    @Expose
    private Integer totalComments;
    @SerializedName("counter")
    @Expose
    private String counter;
    @SerializedName("category_name")
    @Expose
    private String categoryName;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("id_lang")
    @Expose
    private String idLang;
    @SerializedName("image_id")
    @Expose
    private String imageId;
    @SerializedName("image_type")
    @Expose
    private String imageType;
    @SerializedName("image_url")
    @Expose
    private String imageUrl;
    @SerializedName("video")
    @Expose
    private String video;
    @SerializedName("related_product")
    @Expose
    private List <String> relatedProduct = null;
    @SerializedName("related_article")
    @Expose
    private List <String> relatedArticle = null;
    @SerializedName("comments")
    @Expose
    private List <EOMagazineComment> comments = null;
    @SerializedName("content")
    @Expose
    private String content;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getAcceptComment() {
        return acceptComment;
    }

    public void setAcceptComment(String acceptComment) {
        this.acceptComment = acceptComment;
    }

    public Integer getTotalComments() {
        return totalComments;
    }

    public void setTotalComments(Integer totalComments) {
        this.totalComments = totalComments;
    }

    public String getCounter() {
        return counter;
    }

    public void setCounter(String counter) {
        this.counter = counter;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIdLang() {
        return idLang;
    }

    public void setIdLang(String idLang) {
        this.idLang = idLang;
    }

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    public String getImageType() {
        return imageType;
    }

    public void setImageType(String imageType) {
        this.imageType = imageType;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public List <String> getRelatedProduct() {
        return relatedProduct;
    }

    public void setRelatedProduct(List <String> relatedProduct) {
        this.relatedProduct = relatedProduct;
    }

    public List <String> getRelatedArticle() {
        return relatedArticle;
    }

    public void setRelatedArticle(List <String> relatedArticle) {
        this.relatedArticle = relatedArticle;
    }

    public List <EOMagazineComment> getComments() {
        return comments;
    }

    public void setComments(List <EOMagazineComment> comments) {
        this.comments = comments;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
