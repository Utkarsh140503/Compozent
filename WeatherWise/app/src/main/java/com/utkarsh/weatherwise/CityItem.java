package com.utkarsh.weatherwise;

public class CityItem {
    private String cityName;
    private int imageResource;

    public CityItem(String cityName, int imageResource) {
        this.cityName = cityName;
        this.imageResource = imageResource;
    }

    public String getCityName() {
        return cityName;
    }

    public int getImageResource() {
        return imageResource;
    }
}
