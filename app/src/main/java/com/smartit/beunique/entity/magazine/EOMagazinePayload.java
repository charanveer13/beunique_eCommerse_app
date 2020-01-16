package com.smartit.beunique.entity.magazine;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by android on 27/2/19.
 */

public class EOMagazinePayload implements Serializable {

    @SerializedName("category")
    @Expose
    private List <EOMagazineCategory> category = null;
    @SerializedName("article")
    @Expose
    private List <EOMagazineArticle> article = null;

    public List <EOMagazineCategory> getCategory() {
        return category;
    }

    public void setCategory(List <EOMagazineCategory> category) {
        this.category = category;
    }

    public List <EOMagazineArticle> getArticle() {
        return article;
    }

    public void setArticle(List <EOMagazineArticle> article) {
        this.article = article;
    }

}
