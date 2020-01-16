package com.smartit.beunique.entity.dashboardCategory;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by android on 24/1/19.
 */

public class EOCategoryIds implements Serializable {

    @Expose
    @SerializedName("category")
    private EOCategory category;

    public EOCategory getCategory() {
        return category;
    }

    public void setCategory(EOCategory category) {
        this.category = category;
    }

}
