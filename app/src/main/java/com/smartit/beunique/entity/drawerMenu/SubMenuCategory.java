package com.smartit.beunique.entity.drawerMenu;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by android on 11/2/19.
 */

public class SubMenuCategory implements Serializable {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("id_parent")
    @Expose
    private String idParent;
    @SerializedName("name")
    @Expose
    private String name;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getIdParent() {
        return idParent;
    }

    public void setIdParent(String idParent) {
        this.idParent = idParent;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
