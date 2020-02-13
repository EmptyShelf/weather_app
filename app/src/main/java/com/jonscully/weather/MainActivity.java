package com.jonscully.weather;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    TextView temperatureLabel;
    TextView weatherImage;
    Typeface weatherFont;
    Handler mHandler;
    int UPDATE_INTERVAL = (5 * 60 * 1000); // every 5 minutes

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        temperatureLabel = findViewById(R.id.temperatureLabel);
        weatherImage = findViewById(R.id.weatherImage);

        weatherFont = Typeface.createFromAsset(this.getApplicationContext().getAssets(), "fonts/weathericonsRegularWebfont.ttf");
        weatherImage.setTypeface(weatherFont);

        mHandler = new Handler();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                updateWeather();
                mHandler.postDelayed(this, UPDATE_INTERVAL);
            }
        }, 0);
    }

    String getIconForCode(int code) {
        switch ((code / 100)) {
            case 2: // Thunderstorm
                return "\uf01e";
            case 3: // Drizzle
                return "\uf01c";
            case 5: // Rain
                return "\uf019";
            case 6: // Snow
                return "\uf01b";
            case 7: // Atmosphere
                if (code == 762)
                    return "\uf0c8";
                else if (code == 781)
                    return "\uf056";
                else
                    return "\uf014";
            case 8:
                if (code > 800) {
                    return "\uf013"; // Clouds
                }
                else {
                    return "\uf00d"; // Clear
                }
            default:
                return "\uf041";
        }
    }

    void updateWeather() {
        String apiURL = "https://api.openweathermap.org/data/2.5/weather?zip=97206,us&units=imperial&APPID=fef127e7a66510c75e8a195fe08fe72d";

        RequestQueue queue = Volley.newRequestQueue( this );
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, apiURL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                //Toast.makeText(MainActivity.this, (CharSequence) response.toString(), Toast.LENGTH_LONG).show();

                // show response on screen
                try {
                    // get current temperature and update the UI label
                    double temp = response.getJSONObject("main").getDouble("temp");
                    String tempFormatted = getString(R.string.temp_format, temp);
                    temperatureLabel.setText(tempFormatted);

                    // get icon
                    String iconSymbol = getIconForCode(response.getInt("cod"));
                    weatherImage.setText(iconSymbol);
                } catch (JSONException e) {
                    Toast.makeText(MainActivity.this, (CharSequence) e.getMessage(), Toast.LENGTH_LONG).show();
                    //e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        queue.add(request);
    }
}

/**
 *
 *
 * {
 *  "coord":{
 *           "lon":-122.6,
 *           "lat":45.48
 *          },
 *  "weather":[
 *             {
 *              "id":804,
 *              "main":"Clouds",
 *              "description":"overcast clouds",
 *              "icon":"04n"
 *             }
 *            ],
 *  "base":"stations",
 *  "main":{
 *          "temp":48.04,
 *          "feels_like":43.68,
 *          "temp_min":44.01,
 *          "temp_max":51.8,
 *          "pressure":1023,
 *          "humidity":81
 *         },
 *  "visibility":16093,
 *  "wind":{
 *          "speed":4.7,
 *          "deg":220
 *         },
 *  "clouds":{
 *            "all":90
 *           },
 *  "dt":1580970305,
 *  "sys":{
 *         "type":1,
 *         "id":5321,
 *         "country":"US",
 *         "sunrise":1580916418,
 *         "sunset":1580952107
 *        },
 *  "timezone":-28800,
 *  "id":0,
 *  "name":"Portland",
 *  "cod":200
 * }
 *
 *  "weather":[{..., "icon":"04n"}], ...
 * http://openweathermap.org/img/wn/04n@2x.png
 *
 */

