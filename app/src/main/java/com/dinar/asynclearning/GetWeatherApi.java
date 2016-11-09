package com.dinar.asynclearning;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Dr on 09-Nov-16.
 */

public interface GetWeatherApi {

    @GET("/data/2.5/weather")
    Call<Weather> getWeatherFromAPI(
            @Query("q") String city,
            @Query("units") String units,
            @Query("APPID") String appId);

}
