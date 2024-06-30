package com.sunnyweather.android.logic.network

import com.sunnyweather.android.logic.model.Weather
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.ServiceConfigurationError
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

object SunnyWeatherNetwork {
    private val placeService = ServiceCreator.create<PlaceService>()

    suspend fun searchPlaces(query:String) = placeService.searchPlaces(query).await()

    private suspend fun <T> Call<T>.await() : T{
        return suspendCoroutine { continuation ->
            enqueue(object : Callback<T>{
                override fun onResponse(p0: Call<T>, p1: Response<T>) {
                    val body = p1.body()
                    if (body != null) continuation.resume(body)
                    else continuation.resumeWithException(RuntimeException("response body is null!"))
                }

                override fun onFailure(p0: Call<T>, p1: Throwable) {
                    continuation.resumeWithException(p1)
                }
            })
        }
    }

    private val weatherService = ServiceCreator.create(WeatherService::class.java)

    suspend fun getDailyWeather(lng:String, lat:String) =
        weatherService.getDailyWeather(lng, lat).await()

    suspend fun getRealtimeWeather(lng:String, lat:String) =
        weatherService.getRealtimeWeather(lng, lat).await()
}