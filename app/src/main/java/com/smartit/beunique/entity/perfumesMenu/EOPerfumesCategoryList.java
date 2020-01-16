package com.smartit.beunique.entity.perfumesMenu;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by android on 16/2/19.
 */

public class EOPerfumesCategoryList implements Serializable{

    @SerializedName("category")
    @Expose
    private PerfumesCategory category;

    public PerfumesCategory getCategory() {
        return category;
    }

    public void setCategory(PerfumesCategory category) {
        this.category = category;
    }


}
