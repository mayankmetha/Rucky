package com.mayank.rucky.models;

public class HidModel {

    String name;
    int revision;
    String filename;
    String url;
    int state;

    public HidModel(String name, int revision, String filename, String url, int state) {
        this.name = name;
        this.revision = revision;
        this.filename = filename;
        this.url = url;
        this.state = state;
    }

    public String getHidModelName() {
        return name;
    }

    public void setHidModelName(String name) {
        this.name = name;
    }

    public int getHidModelRevision() {
        return revision;
    }

    public void setHidModelRevision(int revision) {
        this.revision = revision;
    }

    public String getHidModelFilename() {
        return filename;
    }

    public void setHidModelFilename(String filename) {
        this.filename = filename;
    }

    public String getHidModelUrl() {
        return url;
    }

    public void setHidModelUrl(String url) {
        this.url = url;
    }

    public int getHidModelState() {
        return state;
    }

    public void setHidModelState(int state) {
        this.state = state;
    }

    public boolean modelExist(HidModel tmpModel) {
        return tmpModel.getHidModelName().equals(this.name);
    }

    public void updateModel(HidModel tmpModel) {
        if(modelExist(tmpModel)) {
            this.name = tmpModel.name;
            this.revision = tmpModel.revision;
            this.filename = tmpModel.filename;
            this.url = tmpModel.url;
            this.state = tmpModel.state;
        }
    }
}
