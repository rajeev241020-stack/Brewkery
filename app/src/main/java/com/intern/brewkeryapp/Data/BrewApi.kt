package com.intern.brewkeryapp.Data



import android.view.MenuItem
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

interface BrewApi {
    @GET("data.json")
    suspend fun getMenu(): MenuResponse

    @GET("api/items/{id}.json")
    suspend fun getItem(@Path("id") id: Int): com.intern.brewkeryapp.Data.MenuItem

    companion object {
        const val BASE_URL = "https://raw.githubusercontent.com/VivekShah138/Brewkery/main/"

        fun create(): BrewApi = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(BrewApi::class.java)
    }
}

class BrewRepository(private val api: BrewApi = BrewApi.create()) {
    suspend fun menu(): Result<MenuResponse> = runCatching { api.getMenu() }
    suspend fun item(id: Int): Result<com.intern.brewkeryapp.Data.MenuItem> = runCatching { api.getItem(id) }
}
