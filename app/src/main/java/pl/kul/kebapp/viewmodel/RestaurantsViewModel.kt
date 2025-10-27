package pl.kul.kebapp.viewmodel

import android.location.Location
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pl.kul.kebapp.R
import pl.kul.kebapp.map.GeocodingService
import pl.kul.kebapp.model.Restaurant
import pl.kul.kebapp.model.Review
import pl.kul.kebapp.model.enums.SortOption
import pl.kul.kebapp.model.enums.SortType
import kotlin.math.roundToInt

class RestaurantsViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val userId: String? = auth.currentUser?.uid

    // UI State
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _restaurantName = MutableStateFlow<String?>(null)
    val restaurantName: StateFlow<String?> = _restaurantName

    private val _reviews = MutableStateFlow<List<Review>>(emptyList())
    val reviews: StateFlow<List<Review>> = _reviews

    private val _reviewStats = MutableStateFlow(ReviewStats())
    val reviewStats: StateFlow<ReviewStats> = _reviewStats

    //Review State
    private val _reviewFormState = MutableStateFlow(ReviewFormState())
    val reviewFormState: StateFlow<ReviewFormState> = _reviewFormState

    //Add Restaurant
    private val _restaurantFormState = MutableStateFlow(AddRestaurantFormState())
    val restaurantFormState: StateFlow<AddRestaurantFormState> = _restaurantFormState

    //Filtrowanie po odległości
    private val _selectedRadiusInKm = MutableStateFlow(10)
    val selectedRadiusInKm: StateFlow<Int> = _selectedRadiusInKm

    fun setSelectedRadius(km: Int) {
        _selectedRadiusInKm.value = km
    }

    //Trzymanie ulubionych restauracji
    private val _favorites = MutableStateFlow<Set<String>>(emptySet())
    val favorites: StateFlow<Set<String>> = _favorites

    //Sortowanie
    private val _sortOption = MutableStateFlow(SortOption(SortType.NAME, ascending = true))
    val sortOption: StateFlow<SortOption> = _sortOption

    init {
        loadFavoritesFromFirestore()
    }

    private fun loadFavoritesFromFirestore() {
        if (userId.isNullOrBlank()) return // brak zalogowanego użytkownika

        db.collection("users")
            .document(userId)
            .get()
            .addOnSuccessListener { doc ->
                val favList = doc.get("favorites") as? List<String> ?: emptyList()
                _favorites.value = favList.toSet()
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Failed to load favorites", e)
            }
    }

    private fun saveFavoritesToFirestore(favorites: Set<String>) {
        if (userId.isNullOrBlank()) return

        db.collection("users")
            .document(userId)
            .update("favorites", favorites.toList())
            .addOnSuccessListener {
                Log.d("Firestore", "Favorites saved")
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Failed to save favorites", e)
            }
    }

    fun toggleFavorite(restaurant: Restaurant) {
        val current = _favorites.value.toMutableSet()
        if (restaurant.id in current) {
            current.remove(restaurant.id)
        } else {
            current.add(restaurant.id)
        }
        _favorites.value = current
        saveFavoritesToFirestore(current)  // zapis do Firestore po każdej zmianie
    }

    fun isFavorite(id: String): Boolean {
        return _favorites.value.contains(id)
    }

    // Add Restaurant
    private fun addRestaurant(
        name: String,
        street: String,
        streetNumber: String,
        postalCode: String,
        city: String,
        country: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val fullAddress = "$street $streetNumber, $postalCode $city, $country"
                val location = geocodeAddress(fullAddress)

                if (location == null) {
                    onFailure(Exception("Address could not be geocoded"))
                    return@launch
                }

                val (lat, lng) = location

                val newRestaurant = hashMapOf(
                    "name" to name,
                    "street" to street,
                    "streetNumber" to streetNumber,
                    "postalCode" to postalCode,
                    "city" to city,
                    "country" to country,
                    "latitude" to lat,
                    "longitude" to lng,
                    "status" to "pending"
                )

                db.collection("restaurants")
                    .add(newRestaurant)
                    .addOnSuccessListener { onSuccess() }
                    .addOnFailureListener { onFailure(it) }

            } catch (e: Exception) {
                onFailure(e)
            }
        }
    }


    private suspend fun geocodeAddress(address: String): Pair<Double, Double>? {
        return try {
            Log.d("Geocoding", "Trying to geocode: $address")

            val response = GeocodingService.api.geocode(
                address = address,
                apiKey = "YOUR_API_KEY_HERE" // Used directly for simplicity during development; should be loaded from a secure source in production
            )

            Log.d("Geocoding", "API returned ${response.results.size} result(s)")

            val location = response.results.firstOrNull()?.geometry?.location

            if (location == null) {
                Log.e("Geocoding", "No results found for address: $address")
            } else {
                Log.d("Geocoding", "Lat: ${location.lat}, Lng: ${location.lng}")
            }

            location?.let { it.lat to it.lng }

        } catch (e: Exception) {
            Log.e("Geocoding", "Geocoding failed", e)
            null
        }
    }

    // Restaurant Details
    fun loadRestaurantDetails(restaurantId: String) {
        _isLoading.value = true

        db.collection("restaurants")
            .document(restaurantId)
            .get()
            .addOnSuccessListener { doc ->
                val restaurant = doc.toObject(Restaurant::class.java)
                _restaurantName.value = restaurant?.name ?: "Unknown Restaurant"
            }

        db.collection("restaurants")
            .document(restaurantId)
            .collection("reviews")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { result ->
                val loadedReviews = result.mapNotNull { doc ->
                    val review = doc.toObject(Review::class.java)
                    review.copy(id = doc.id) // <- nadanie ID z dokumentu
                }
                _reviews.value = loadedReviews
                _isLoading.value = false

                if (loadedReviews.isNotEmpty()) {
                    _reviewStats.value = calculateStats(loadedReviews)
                }
            }
            .addOnFailureListener {
                _isLoading.value = false
            }
    }

    fun getReviewsForRestaurant(
        restaurantId: String,
        onResult: (List<Review>) -> Unit
    ) {
        db.collection("restaurants")
            .document(restaurantId)
            .collection("reviews")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { result ->
                val reviews = result.mapNotNull { doc ->
                    doc.toObject(Review::class.java).copy(id = doc.id)
                }
                onResult(reviews)
            }
            .addOnFailureListener {
                onResult(emptyList()) // Zwracamy pustą listę przy błędzie
            }
    }

    // Average and percentage calculations
    private fun calculateStats(reviews: List<Review>): ReviewStats {
        val avgTaste = reviews.map { it.tasteRating }.average()
        val avgQuality = reviews.map { it.qualityRating }.average()
        val avgVenue = reviews.map { it.venueRating }.average()
        val avgValue = reviews.map { it.valueForMoneyRating }.average()
        val avgOverall = reviews.map { it.overallRating }.average()
        val repeatCount = reviews.count { it.wouldRepeat }
        val repeatPercent = (repeatCount.toDouble() / reviews.size) * 100

        return ReviewStats(
            avgTaste = avgTaste,
            avgQuality = avgQuality,
            avgVenue = avgVenue,
            avgValueForMoney = avgValue,
            avgOverall = avgOverall,
            repeatPercentage = repeatPercent
        )
    }

    // Load all restaurants (used in lists)
    fun getAllApprovedRestaurants(onRestaurantsLoaded: (List<Restaurant>) -> Unit) {
        db.collection("restaurants")
            .get()
            .addOnSuccessListener { result ->
                val restaurants = result.mapNotNull { doc ->
                    val restaurant = doc.toObject(Restaurant::class.java)
                    if (restaurant.status == "approved") restaurant.copy(id = doc.id) else null
                }
                onRestaurantsLoaded(restaurants)
            }
            .addOnFailureListener { e ->
                Log.e("FireStore", "Error loading restaurants", e)
                onRestaurantsLoaded(emptyList())
            }
    }

    //to trzeba będzie zmienić - żeby pobierało wszystkie restauracje,ale admin mógł sobie sortować
    //bo należy pamniętać, że admin powienen móc też zmieniać status restauracji
    fun getPendingRestaurants(onLoaded: (List<Restaurant>) -> Unit) {
        db.collection("restaurants")
            .get()
            .addOnSuccessListener { result ->
                val pending = result.mapNotNull { doc ->
                    val restaurant = doc.toObject(Restaurant::class.java)
                    if (restaurant.status == "pending") restaurant.copy(id = doc.id) else null
                }
                onLoaded(pending)
            }
            .addOnFailureListener {
                Log.e("FireStore", "Failed to load pending restaurants", it)
                onLoaded(emptyList())
            }
    }

    fun getRestaurantById(restaurantId: String, onLoaded: (Restaurant?) -> Unit) {
        db.collection("restaurants")
            .document(restaurantId)
            .get()
            .addOnSuccessListener { doc ->
                val restaurant = doc.toObject(Restaurant::class.java)
                if (restaurant != null) {
                    onLoaded(restaurant.copy(id = doc.id))
                } else {
                    onLoaded(null)
                }
            }
            .addOnFailureListener {
                Log.e("FireStore", "Error loading restaurant by id", it)
                onLoaded(null)
            }
    }

    fun deleteReview(restaurantId: String, reviewId: String) {
        db.collection("restaurants")
            .document(restaurantId)
            .collection("reviews")
            .document(reviewId)
            .delete()
            .addOnSuccessListener {
                updateAverageRating(restaurantId)
                loadRestaurantDetails(restaurantId)
            }
            .addOnFailureListener {
                Log.e("FireStore", "Failed to delete review", it)
            }
    }

    //Status dla admina
    fun updateRestaurantStatus(restaurantId: String, newStatus: String) {
        db.collection("restaurants")
            .document(restaurantId)
            .update("status", newStatus)
            .addOnSuccessListener {
                Log.d("FireStore", "Status updated to $newStatus")
            }
            .addOnFailureListener {
                Log.e("FireStore", "Failed to update status", it)
            }
    }

    // Add a review

    fun updateTaste(value: Float) = _reviewFormState.update { it.copy(taste = value) }
    fun updateQuality(value: Float) = _reviewFormState.update { it.copy(quality = value) }
    fun updateVenue(value: Float) = _reviewFormState.update { it.copy(venue = value) }
    fun updateValueForMoney(value: Float) =
        _reviewFormState.update { it.copy(valueForMoney = value) }

    fun updateOverall(value: Float) = _reviewFormState.update { it.copy(overall = value) }
    fun updateComment(value: String) =
        _reviewFormState.update { it.copy(comment = value.take(500)) }

    fun updateWouldRepeat(value: Boolean) = _reviewFormState.update { it.copy(wouldRepeat = value) }

    fun isReviewValid(userId: String?): Boolean {
        val state = _reviewFormState.value
        return userId != null && state.comment.length in 5..500
    }

    fun submitReview(
        restaurantId: String,
        userId: String,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {
        val state = _reviewFormState.value
        _reviewFormState.update { it.copy(isSubmitting = true) }

        val review = Review(
            userID = userId,
            timestamp = Timestamp.now(),
            tasteRating = state.taste.roundToInt(),
            qualityRating = state.quality.roundToInt(),
            venueRating = state.venue.roundToInt(),
            valueForMoneyRating = state.valueForMoney.roundToInt(),
            overallRating = state.overall.roundToInt(),
            comment = state.comment,
            wouldRepeat = state.wouldRepeat
        )

        addReview(
            restaurantId = restaurantId,
            review = review,
            onSuccess = {
                _reviewFormState.value = ReviewFormState() // reset
                onSuccess()
            },
            onFailure = {
                _reviewFormState.update { it.copy(isSubmitting = false) }
                onFailure()
            }
        )
    }

    private fun addReview(
        restaurantId: String,
        review: Review,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val restaurantRef = db.collection("restaurants").document(restaurantId)

        restaurantRef.collection("reviews")
            .add(review)
            .addOnSuccessListener {
                updateAverageRating(restaurantId)
                onSuccess()
            }
            .addOnFailureListener { onFailure(it) }
    }

    private fun updateAverageRating(restaurantId: String) {
        val reviewsRef = db.collection("restaurants").document(restaurantId).collection("reviews")

        reviewsRef.get()
            .addOnSuccessListener { result ->
                val reviews = result.mapNotNull { it.toObject(Review::class.java) }
                if (reviews.isNotEmpty()) {
                    val avgOverall = reviews.map { it.overallRating }.average()

                    db.collection("restaurants")
                        .document(restaurantId)
                        .update("averageRating", avgOverall)
                        .addOnSuccessListener {
                            Log.d("FireStore", "Average rating updated: $avgOverall")
                        }
                        .addOnFailureListener {
                            Log.e("FireStore", "Failed to update average rating", it)
                        }
                }
            }
            .addOnFailureListener {
                Log.e("FireStore", "Failed to fetch reviews for average rating", it)
            }
    }

    //Add Restaurant

    fun updateName(value: String) = _restaurantFormState.update { it.copy(name = value) }
    fun updateStreet(value: String) = _restaurantFormState.update { it.copy(street = value) }
    fun updateStreetNumber(value: String) =
        _restaurantFormState.update { it.copy(streetNumber = value) }

    fun updatePostalCode(value: String) =
        _restaurantFormState.update { it.copy(postalCode = value) }

    fun updateCity(value: String) = _restaurantFormState.update { it.copy(city = value) }

    fun submitRestaurant(onSuccess: () -> Unit) {
        val state = _restaurantFormState.value
        val errors = validateRestaurantForm(state)
        if (errors.hasErrors()) {
            _restaurantFormState.update { it.copy(errors = errors) }
            return
        }

        _restaurantFormState.update { it.copy(isSubmitting = true, errorMessage = null) }

        addRestaurant(
            name = state.name,
            street = state.street,
            streetNumber = state.streetNumber,
            postalCode = state.postalCode,
            city = state.city,
            country = "Poland",
            onSuccess = {
                _restaurantFormState.value = AddRestaurantFormState()
                onSuccess()
            },
            onFailure = { e ->
                _restaurantFormState.update {
                    it.copy(isSubmitting = false, errorMessage = e.message)
                }
            }
        )
    }

    private fun validateRestaurantForm(state: AddRestaurantFormState): RestaurantFormErrors {
        return RestaurantFormErrors(
            name = if (state.name.isBlank()) "Name is required" else null,
            street = if (state.street.isBlank()) "Street is required"
            else if (!state.street.matches(Regex("^[\\p{L}\\d\\s\\-]+$"))) "Invalid characters" else null,
            streetNumber = if (state.streetNumber.isBlank()) "Street number is required"
            else if (!state.streetNumber.matches(Regex("^\\d+[A-Za-z]?$"))) "Invalid format (e.g. 4A)" else null,
            postalCode = if (state.postalCode.isBlank()) "Postal code is required"
            else if (!state.postalCode.matches(Regex("^\\d{2}-\\d{3}$"))) "Format: XX-XXX" else null,
            city = if (state.city.isBlank()) "City is required"
            else if (!state.city.matches(Regex("^[\\p{L}\\s\\-]+$"))) "Invalid city name" else null
        )
    }

    //obliczanie odległości pomiędzy dwoma punktami
    fun distanceBetween(
        lat1: Double, lon1: Double,
        lat2: Double, lon2: Double
    ): Float {
        val result = FloatArray(1)
        Location.distanceBetween(lat1, lon1, lat2, lon2, result)
        return result[0] // w metrach
    }

    //Filtrowanie po odległości
    fun filterRestaurantsByLocation(
        allRestaurants: List<Restaurant>,
        userLocation: Location?
    ): List<Restaurant> {
        val radius = _selectedRadiusInKm.value
        return if (userLocation != null) {
            allRestaurants.filter { r ->
                r.latitude != null && r.longitude != null &&
                        distanceBetween(
                            userLocation.latitude, userLocation.longitude,
                            r.latitude!!, r.longitude!!
                        ) <= radius * 1000
            }
        } else emptyList()
    }

    //Sortowanie
    fun setSortOption(type: SortType) {
        val current = _sortOption.value

        if (current.type == type) {
            // Kliknięcie w ten sam typ – zmieniamy kierunek
            _sortOption.value = current.copy(ascending = !current.ascending)
        } else {
            // Nowy typ – domyślny kierunek zależny od typu
            val defaultAscending = when (type) {
                SortType.NAME -> true
                SortType.RATING -> false //domyślnie malejąco
                SortType.DISTANCE -> true
            }
            _sortOption.value = SortOption(type = type, ascending = defaultAscending)
        }
    }

    fun sortRestaurants(
        restaurants: List<Restaurant>,
        userLocation: Location?,
        sortOption: SortOption
    ): List<Restaurant> {
        return when (sortOption.type) {
            SortType.NAME -> {
                if (sortOption.ascending)
                    restaurants.sortedBy { it.name }
                else
                    restaurants.sortedByDescending { it.name }
            }

            SortType.RATING -> {
                if (sortOption.ascending)
                    restaurants.sortedBy { it.averageRating ?: 0.0 }
                else
                    restaurants.sortedByDescending { it.averageRating ?: 0.0 }
            }

            SortType.DISTANCE -> {
                if (userLocation == null) return restaurants
                val withDistance = restaurants.map { r ->
                    val distance = if (r.latitude != null && r.longitude != null) {
                        distanceBetween(
                            userLocation.latitude,
                            userLocation.longitude,
                            r.latitude!!,
                            r.longitude!!
                        )
                    } else Float.MAX_VALUE
                    r to distance
                }
                val sorted = if (sortOption.ascending)
                    withDistance.sortedBy { it.second }
                else
                    withDistance.sortedByDescending { it.second }
                sorted.map { it.first }
            }
        }
    }

    fun sortReviewsByDate(
        reviewsWithRestaurants: List<Pair<Restaurant, Review>>,
        ascending: Boolean = false // domyślnie false, czyli najnowsze na górze
    ): List<Pair<Restaurant, Review>> {
        return if (ascending) {
            reviewsWithRestaurants.sortedBy { it.second.timestamp }
        } else {
            reviewsWithRestaurants.sortedByDescending { it.second.timestamp }
        }
    }

    fun getAllReviewsSortedByDate(onResult: (List<Pair<Restaurant, Review>>) -> Unit) {
        getAllApprovedRestaurants { restaurants: List<Restaurant> ->
            val allReviews = mutableListOf<Pair<Restaurant, Review>>()
            var loadedCount = 0

            if (restaurants.isEmpty()) {
                onResult(emptyList())
                return@getAllApprovedRestaurants
            }

            restaurants.forEach { restaurant ->
                getReviewsForRestaurant(restaurant.id) { reviews: List<Review> ->
                    reviews.forEach { review ->
                        allReviews.add(restaurant to review)
                    }
                    loadedCount++
                    if (loadedCount == restaurants.size) {
                        val sortedReviews = sortReviewsByDate(allReviews, ascending = false)
                        onResult(sortedReviews)
                    }
                }
            }
        }
    }
}

data class ReviewStats(
    val avgTaste: Double = 0.0,
    val avgQuality: Double = 0.0,
    val avgVenue: Double = 0.0,
    val avgValueForMoney: Double = 0.0,
    val avgOverall: Double = 0.0,
    val repeatPercentage: Double = 0.0
)

data class ReviewFormState(
    val taste: Float = 5f,
    val quality: Float = 5f,
    val venue: Float = 5f,
    val valueForMoney: Float = 5f,
    val overall: Float = 5f,
    val comment: String = "",
    val wouldRepeat: Boolean = false,
    val isSubmitting: Boolean = false
)

data class AddRestaurantFormState(
    val name: String = "",
    val street: String = "",
    val streetNumber: String = "",
    val postalCode: String = "",
    val city: String = "",
    val isSubmitting: Boolean = false,
    val errorMessage: String? = null,
    val errors: RestaurantFormErrors = RestaurantFormErrors()
)

data class RestaurantFormErrors(
    val name: String? = null,
    val street: String? = null,
    val streetNumber: String? = null,
    val postalCode: String? = null,
    val city: String? = null
)

fun RestaurantFormErrors.hasErrors(): Boolean =
    name != null || street != null || streetNumber != null || postalCode != null || city != null