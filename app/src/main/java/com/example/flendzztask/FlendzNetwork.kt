package com.example.flendzztask

import com.google.gson.GsonBuilder
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.*

interface FlendzNetwork {

    @GET("users")
    fun getEmployeeList(): Call<String>

    @GET("users/{id}")
    fun getEmployee(@Path("id") id:Int): Call<String>

}


private const val BASE_URL = "https://jsonplaceholder.typicode.com"

private val retrofit = Retrofit.Builder()
    .addConverterFactory(ScalarsConverterFactory.create())
    .addConverterFactory(GsonConverterFactory.create(
        GsonBuilder()
        .setLenient()
        .create()
    ))
    .baseUrl(BASE_URL)
    .build()

object FlendzApi {
    val retrofitService : FlendzNetwork by lazy {
        retrofit.create(FlendzNetwork::class.java) }
}
