package com.example.plant_iot_phone;

public class PlantListItem {
    String name, model;
    int resId, informId;

    public PlantListItem(String name, String model, int informId, int resId) {
        this.name = name;
        this.model = model;
        this.informId = informId;
        this.resId = resId;
    }

    public String getName() {return name;}
    public void setName(String name) {this.name = name;}
    public String getModel() {return model;}
    public void setModel(String model) {this.model = model;}
    public int getInformId() {return informId;}
    public void setInformId(int informId) {this.informId = informId;}
    public int getResId() {return resId;}
    public void setResId(int resId) {this.resId = resId;}
}
