package com.example.app19_jc_neon_bd_remoto.model

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    // Deja solo una BASE_URL:
    // Emulador: 10.0.2.2 es la PC donde corre la API -> "http://10.0.2.2:3000/"
    // Celular físico: la IP de tu PC en la misma red Wi-Fi (se ve con ipconfig)
    // API publicada: "https://tu-api.onrender.com/"
    private const val BASE_URL = "http://192.168.2.4:3000/"

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY   // muestra request y response en Logcat (filtro: okhttp)
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(logging)
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    val api: PersonaApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PersonaApiService::class.java)
    }
}
