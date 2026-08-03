package abhishek.gupta.weatherapp.domain.domainModel


data class DomainSuggestedCity(
    val city: String,
    val name: String,
    val state: String?,
    val country: String,
    val addressLine1: String,
    val addressLine2: String,
    val countryCode: String,
    val formatted: String,
    val postcode: String,
    )