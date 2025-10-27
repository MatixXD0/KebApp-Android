package pl.kul.kebapp.model

import com.google.firebase.Timestamp

data class Review(
    val id: String? = null,
    val userID: String = "",
    val timestamp: Timestamp = Timestamp.now(),
    val tasteRating: Int = 0,           // Smak
    val qualityRating: Int = 0,          // Jakość
    val venueRating: Int = 0,            // Lokal
    val valueForMoneyRating: Int = 0,    // Stosunek jakość/cena
    val comment: String = "",
    val overallRating: Int = 0,          // Ogólna ocena
    val wouldRepeat: Boolean = false,    // Czy powtórzyłbyś?
)
