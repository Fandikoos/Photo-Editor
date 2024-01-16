package com.svalero.downloader.task;

import java.util.ArrayList;
import java.util.List;

public class HistorialTask {
    private String imageName;
    private List<String> appliedFilters;

    public HistorialTask(String imageName, List<String> appliedFilters){
        this.imageName = imageName;
        this.appliedFilters = appliedFilters;
    }

    public String getImageName(){
        return imageName;
    }

    public List<String> getAppliedFilters(){
        return new ArrayList<>(appliedFilters);
    }
}
