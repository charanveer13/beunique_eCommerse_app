package com.smartit.beunique.entity.search;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by android on 20/3/19.
 */

public class EOSearchPayload implements Serializable {

    @SerializedName("total_results_found")
    @Expose
    private Integer totalResultsFound;
    @SerializedName("offset")
    @Expose
    private Integer offset;
    @SerializedName("data")
    @Expose
    private List<EOSearchData> data = null;

    public Integer getTotalResultsFound() {
        return totalResultsFound;
    }

    public void setTotalResultsFound(Integer totalResultsFound) {
        this.totalResultsFound = totalResultsFound;
    }

    public Integer getOffset() {
        return offset;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    public List<EOSearchData> getData() {
        return data;
    }

    public void setData(List<EOSearchData> data) {
        this.data = data;
    }

}
