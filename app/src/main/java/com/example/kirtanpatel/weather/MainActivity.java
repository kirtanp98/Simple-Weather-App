package com.example.kirtanpatel.weather;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.concurrent.ExecutionException;

import javax.net.ssl.HttpsURLConnection;


public class MainActivity extends AppCompatActivity {

    private FusedLocationProviderClient mFusedLocationClient;
    public final int MY_PERMISSIONS_REQUEST_LOCATION = 0;
    private double[] coordinates = new double[2];
    public String output;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //implmentation of a refresh button on the action button
        //TODO finish making an refresh button on the actionbar
        setContentView(R.layout.activity_main);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);


        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        //Checks for permission

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)) {

                // Show an explanation to the user *asynchronously
                //try to ask for permission again

            } else {
                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
        }

        //starts getting location on start up
        getLocation();

        try {
            output = new GetJsonFromURL().execute("https://api.darksky.net/forecast/keyhere/" + coordinates[0] +"," + coordinates[1]).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
            output = "Got interupted";
        } catch (ExecutionException e) {
            e.printStackTrace();
            output = "Didn't execute";
        }
        TextView temp = (TextView) findViewById(R.id.temp);

        Gson gson = new Gson();
        WeatherObject test = gson.fromJson(output,WeatherObject.class);

        //gets the day
        Calendar today = Calendar.getInstance();
        int date = today.get(Calendar.DAY_OF_WEEK);

        //sets date
        String dateString = getDate(date);
        TextView dateText = (TextView) findViewById(R.id.dateXML);
        dateText.setText(dateString);

        //setting temperature in Fahrenheit
        String degree  = "\u00b0";
        temp.setText(""+test.getTemperature() + degree);

        //setting weather text
        TextView weatherText = (TextView) findViewById(R.id.weatherText);
        String weatherCurrent = getWeatherString(test.getIcon());
        weatherText.setText(weatherCurrent);

        //setting image
        ImageView weatherImage = (ImageView) findViewById(R.id.WeatherImage);
        getWeatherIcon(weatherImage, test.getIcon());


        Button press = (Button) findViewById(R.id.press);
        press.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getLocation();
                TextView temp = (TextView) findViewById(R.id.temp);

                try {
                    output = new GetJsonFromURL().execute("https://api.darksky.net/forecast/6fbcf7b85b9cd3021c8a39883c63389d/" + coordinates[0] +"," + coordinates[1]).get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    output = "Got interupted";
                } catch (ExecutionException e) {
                    e.printStackTrace();
                    output = "Didn't execute";
                }

                //Makes the json test returned from a string into an object
                Gson gson = new Gson();
                WeatherObject test = gson.fromJson(output,WeatherObject.class);

                //gets the day
                Calendar rightnow = Calendar.getInstance();
                int date = rightnow.get(Calendar.DAY_OF_WEEK);

                //sets the day
                String dateString = getDate(date);
                TextView dateText = (TextView) findViewById(R.id.dateXML);
                dateText.setText(dateString);

                //sets temperature
                String degree  = "\u00b0";
                temp.setText(""+test.getTemperature() + degree);

                //sets weather text
                TextView weatherText = (TextView) findViewById(R.id.weatherText);
                String weatherCurrent = getWeatherString(test.getIcon());
                weatherText.setText(weatherCurrent);

                //setting image
                ImageView weatherImage = (ImageView) findViewById(R.id.WeatherImage);
                getWeatherIcon(weatherImage, test.getIcon());
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        //permission was granted

                } else {

                    // permission denied
                    //Need to include some type of management if the user denies location.
                }
                return;
            }

        }
    }

    //gets locations using fused location
    public void getLocation(){

        try {
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location.
                            if (location != null) {
                                // Logic to handle location object
                                coordinates[0] = location.getLatitude();
                                Log.i("LAT", coordinates[0]+"");
                                coordinates[1] = location.getLongitude();
                                Log.i("LONG", coordinates[1]+"");
                            }
                            else {

                            }
                        }
                    });
        }
        catch (SecurityException e){

        }

    }



    public static String getJsonOutput(String url){
        HttpsURLConnection con = null;
        try {
            URL u = new URL(url);
            con = (HttpsURLConnection) u.openConnection();

            con.connect();


            BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line + "\n");
            }
            br.close();
            return sb.toString();


        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (con != null) {
                try {
                    con.disconnect();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
        return null;
    }

    public static String getDate(int date){
        switch (date){
            case 1: return "Sunday";
            case 2: return "Monday";
            case 3: return "Tuesday";
            case 4: return "Wednesday";
            case 5: return "Thursday";
            case 6: return "Friday";
            case 7: return "Saturday";
        }
        return "ERROR AT getDATE";
    }

    //gets icons

    public static void getWeatherIcon(ImageView weather, String icon){
        Log.v("icon",icon);
        //icon.replaceAll("[^a-zA-Z0-9]", "");
        Log.v("icon_Replaced",icon);


        switch (icon){
            case "clear-day": weather.setImageResource(R.drawable.ic_weather_sunny_black_48dp);
                break;
            case "clear-night" : weather.setImageResource(R.drawable.ic_weather_night_black_48dp);
                break;
            case "rain" : weather.setImageResource(R.drawable.ic_weather_rainy_black_48dp);
                break;
            case "snow" : weather.setImageResource(R.drawable.ic_weather_snowy_black_48dp);
                break;
            case "sleet" : weather.setImageResource(R.drawable.ic_weather_snowy_rainy_black_48dp);
                break;
            case "wind" : weather.setImageResource(R.drawable.ic_weather_windy_black_48dp);
                break;
            case "fog" : weather.setImageResource(R.drawable.ic_weather_fog_black_48dp);
                break;
            case "cloudy" : weather.setImageResource(R.drawable.ic_weather_cloudy_black_48dp);
                break;
            case "partly-cloudy-day" : weather.setImageResource(R.drawable.ic_weather_partlycloudy_black_48dp);
                break;
            case "partly-cloudy-night" : weather.setImageResource(R.drawable.ic_weather_sunny_black_48dp);
                break;
            case "hail" : weather.setImageResource(R.drawable.ic_weather_hail_black_48dp);
                break;
            case "thunderstorm" : weather.setImageResource(R.drawable.ic_weather_lightning_rainy_black_48dp);
                break;
            case "tornado" : weather.setImageResource(R.drawable.ic_weather_sunny_black_48dp);
                break;//TODO find a tornado icon
            default : weather.setImageResource(R.drawable.ic_weather_sunny_white_48dp);
                break;


        }
    }

    public static String getWeatherString(String s){
        switch (s){
            case "clear-day": return "Clear";
            case "clear-night" : return "Clear";
            case "rain" : return "Rain";
            case "snow" : return "Snow";
            case "sleet" : return "Sleet";
            case "wind" : return "Wind";
            case "fog" : return "Fog";
            case "cloudy" : return "Cloudy";
            case "partly-cloudy-day" : return "Partly Cloudy";
            case "partly-cloudy-night" : return "Partly Cloudy";
            case "hail" : return "Hail";
            case "thunderstorm" : return "Thunderstorm";
            case "tornado" : return "Tornado";
            //TODO find a tornado icon
            default : return "ERROR";
        }

    }



}
