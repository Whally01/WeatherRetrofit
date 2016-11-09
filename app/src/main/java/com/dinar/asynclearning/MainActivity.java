package com.dinar.asynclearning;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Dr on 09-Nov-16.
 */

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String API_URL = "http://api.openweathermap.org";
    private static final String CITY_NAME = "Kazan";
    private static final String UNITS = "metric";
    private static final String APP_ID = "7d458b13bce6e079797e727daf845145";

    private Button getKZNSync, getKZNAsync;
    private TextView temp_tv, pressure_tv, humidity_tv;
    private ProgressBar loadingProgressBar;
    private LinearLayout getWeatherLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getKZNSync = (Button) findViewById(R.id.sync_btn);
        getKZNAsync = (Button) findViewById(R.id.async_btn);
        getWeatherLayout = (LinearLayout) findViewById(R.id.root_ll);
        temp_tv = (TextView) findViewById(R.id.temperature_tv);
        pressure_tv = (TextView) findViewById(R.id.pressure_tv);
        humidity_tv = (TextView) findViewById(R.id.humidity_tv);
        loadingProgressBar = (ProgressBar) findViewById(R.id.pb);

        getKZNSync.setOnClickListener(this);
        getKZNAsync.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.sync_btn:
                getWeatherSync();
                break;

            case R.id.async_btn:
                getWeatherAsync();
                break;
        }
    }

    private void getWeatherAsync() {
        getWeatherLayout.setVisibility(View.INVISIBLE);
        loadingProgressBar.setVisibility(View.VISIBLE);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        GetWeatherApi getWeatherApi = retrofit.create(GetWeatherApi.class);

        Call<Weather> call;
        call = getWeatherApi.getWeatherFromAPI(CITY_NAME, UNITS, APP_ID);
        call.enqueue(new Callback<Weather>() {

            @Override
            public void onResponse(Call<Weather> call, Response<Weather> response) {
                updateUI(response.body());
            }

            @Override
            public void onFailure(Call<Weather> call, Throwable t) {
                loadingProgressBar.setVisibility(View.GONE);
                Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void updateUI(Weather weather) {
        loadingProgressBar.setVisibility(View.GONE);
        if (weather != null) {
            getWeatherLayout.setVisibility(View.VISIBLE);
            temp_tv.setText("Температура : " + weather.getMain().getTemp() + " C°");
            pressure_tv.setText("Атмосферное давление : " + weather.getMain().getPressure() + " мм р.с.");
            humidity_tv.setText("Влажность : " + weather.getMain().getHumidity() + "%");
        }
    }

    private void getWeatherSync() {
        GetWeatherSync getWeatherSync = new GetWeatherSync();
        getWeatherSync.execute();
    }

    private class GetWeatherSync extends AsyncTask<Void, Void, Weather> {

        Retrofit retrofit;

        @Override
        protected void onPreExecute() {
            getWeatherLayout.setVisibility(View.INVISIBLE);
            loadingProgressBar.setVisibility(View.VISIBLE);

            retrofit = new Retrofit.Builder()
                    .baseUrl(API_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        @Override
        protected Weather doInBackground(Void... params) {
            Weather weather = null;
            try {

                GetWeatherApi getWeatherApi = retrofit.create(GetWeatherApi.class);
                Call<Weather> call;
                call = getWeatherApi.getWeatherFromAPI(CITY_NAME, UNITS, APP_ID);
                Response<Weather> response;
                response = call.execute();
                weather = response.body();

            } catch (final IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loadingProgressBar.setVisibility(View.GONE);
                        Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            return weather;
        }

        @Override
        protected void onPostExecute(Weather weather) {
            updateUI(weather);

        }
    }
}