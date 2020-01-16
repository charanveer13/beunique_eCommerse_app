package com.smartit.beunique.entity.allproducts;

/**
 * Created by android on 18/1/19.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class EOProductCategory implements Serializable {

    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("id_parent")
    @Expose
    private String id_parent;

    public String getId_parent() {
        return id_parent;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @SerializedName("name")

    @Expose
    private String name;

    public void setId_parent(String id_parent) {
        this.id_parent = id_parent;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
