package com.smartit.beunique.entity.drawerMenu;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by android on 11/2/19.
 */

public class EOPerfumesSubMenu implements Serializable {

    @SerializedName("categories")
    @Expose
    private List <SubMenuCategory> categories = null;

    public List <SubMenuCategory> getCategories() {
        return categories;
    }

    public void setCategories(List <SubMenuCategory> categories) {
        this.categories = categories;
    }

}
