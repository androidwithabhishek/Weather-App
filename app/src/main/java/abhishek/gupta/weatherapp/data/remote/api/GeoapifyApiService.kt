package abhishek.gupta.weatherapp.data.remote.api

import abhishek.gupta.weatherapp.data.remote.dto.citySuggestionDto.GeoapifyResponse

import android.R.attr.apiKey
import retrofit2.http.GET
import retrofit2.http.Query


//https://api.geoapify.com/v1/geocode/autocomplete?text=moradaba&apiKey=78d13d6195ca48958557af6815a3f05c

interface GeoapifyApiService {

    @GET("autocomplete")
  suspend  fun getCitySuggestion(
        @Query("text") city: String,
        @Query("apiKey") apiKey: String,
    ): GeoapifyResponse

}