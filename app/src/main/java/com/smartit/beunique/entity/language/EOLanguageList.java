package com.smartit.beunique.entity.language;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by android on 1/2/19.
 */

public class EOLanguageList implements Serializable{

    @SerializedName("languages")
    @Expose
    private List<EOLanguage> languages = null;

    public List<EOLanguage> getLanguages() {
        return languages;
    }

    public void setLanguages(List<EOLanguage> languages) {
        this.languages = languages;
    }

}
