package abhishek.gupta.weatherapp.data.mapper

import abhishek.gupta.weatherapp.data.remote.dto.citySuggestionDto.GeoapifyResponse
import abhishek.gupta.weatherapp.domain.domainModel.DomainSuggestedCity
import com.google.firebase.firestore.pipeline.Expression.Companion.map

fun GeoapifyResponse.toDomainSuggestedCity(): List<DomainSuggestedCity> {


    return this.features.map { feature ->
        DomainSuggestedCity(
            city = feature.properties.city?:"",
            name = feature.properties.name?:"",
            state = feature.properties.state?:"",
            country = feature.properties.country?:"",
            addressLine1 = feature.properties.address_line1?:"",
            addressLine2 = feature.properties.address_line1?:"",
            countryCode = feature.properties.country_code?:"",
            formatted = feature.properties.formatted?:"",
            postcode = feature.properties.postcode?:"",
        )
    }
    
    
    
}