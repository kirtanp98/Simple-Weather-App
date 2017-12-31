package com.example.kirtanpatel.weather;

/**
 * Created by kirtanpatel on 8/28/17.
 */

public class WeatherObject {
    private double latitude;
    private double longitude;
    private String timezone;
    private currently currently;


    public class currently{
        private int time;
        private String summary;
        private String icon;
        private int nearestStormDistance;
        private double precipIntensity;
        private double precipIntensityError;
        private double precipProbability;
        private String precipType;
        private double temperature;
    }

    public double getTemperature(){
        int temp = (int)(currently.temperature + 1);
        return temp;
    }

    public String getIcon(){
        return currently.icon;
    }

}
