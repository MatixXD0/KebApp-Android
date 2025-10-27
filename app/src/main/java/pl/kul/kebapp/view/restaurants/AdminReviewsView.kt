import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import pl.kul.kebapp.R
import pl.kul.kebapp.model.Restaurant
import pl.kul.kebapp.model.Review
import pl.kul.kebapp.view.restaurants.InfoRow
import pl.kul.kebapp.viewmodel.RestaurantsViewModel

@Composable
fun AdminReviewsView(
    viewModel: RestaurantsViewModel,
) {
    val reviewsState = remember { mutableStateOf<List<Pair<Restaurant, Review>>>(emptyList()) }
    val isLoading by viewModel.isLoading.collectAsState()

    // Funkcja asynchronicznie pobierająca i sortująca recenzje po dacie
    fun refreshReviews() {
        viewModel.getAllReviewsSortedByDate { sortedReviews ->
            reviewsState.value = sortedReviews
        }
    }

    LaunchedEffect(Unit) {
        refreshReviews()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = stringResource(R.string.all_reviews),
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        } else {
            if (reviewsState.value.isEmpty()) {
                Text(
                    text = stringResource(R.string.no_reviews_yet),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    reviewsState.value.forEach { (restaurant, review) ->
                        ReviewCardWithRestaurantName(
                            restaurantName = restaurant.name,
                            review = review,
                            onDelete = {
                                viewModel.deleteReview(restaurant.id, review.id ?: "")
                                // Po usunięciu odśwież listę
                                refreshReviews()
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ReviewCardWithRestaurantName(
    restaurantName: String,
    review: Review,
    onDelete: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = restaurantName,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(8.dp))

            InfoRow(stringResource(R.string.user_name), review.userID)
            InfoRow(stringResource(R.string.comment_name), review.comment)

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onDelete,
                colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.errorContainer),
                modifier = Modifier.align(Alignment.End)
            ) {
                Text(
                    text = stringResource(R.string.delete_review),
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }
    }
}
