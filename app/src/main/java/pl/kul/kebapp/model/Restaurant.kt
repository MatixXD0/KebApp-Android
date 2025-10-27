package pl.kul.kebapp.model

data class Restaurant(
    val id: String = "", //to pole jest ustawiane ręcznie podczas pobierania recenzji, nie znajduje się w bazie danych
    val name: String = "",

    // Address details
    val street: String = "",
    val streetNumber: String = "",
    val postalCode: String = "",
    val city: String = "",
    val country: String = "Poland", // default for now

    // Geolocation
    val latitude: Double? = null,
    val longitude: Double? = null,

    val averageRating: Double? = null,

    //var isFavorite: Boolean = false //dziala lokalnie, nie zmienia danych z API

    val status: String = "pending"
) {
    // get full address string
    fun fullAddress(): String {
        return "$street $streetNumber, $postalCode $city, $country"
    }
}
