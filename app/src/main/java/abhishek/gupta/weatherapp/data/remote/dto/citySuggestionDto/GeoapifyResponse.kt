package abhishek.gupta.weatherapp.data.remote.dto.citySuggestionDto

data class GeoapifyResponse(
    val features: List<Feature>,
    val query: Query,
    val type: String
)