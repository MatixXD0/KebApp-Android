package pl.kul.kebapp.view.restaurants.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import pl.kul.kebapp.R
import pl.kul.kebapp.model.Restaurant
import pl.kul.kebapp.model.Review
import pl.kul.kebapp.ui.theme.Red
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun ReviewCard(
    review: Review,
    userRole: String?,
    onDeleteConfirmed: (String) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // User + Date
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                //For Now - take - first 10 sign
                //TODO change user id to email or username
                Text("User: ${review.userID.take(10)}", style = MaterialTheme.typography.bodyMedium)
                Text(
                    text = formatTimestamp(review.timestamp),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            OverallRatingBadge(overallRating = review.overallRating)

            // Aspects in two columns
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    RatingBadge(label = stringResource(R.string.taste), value = review.tasteRating)
                    RatingBadge(
                        label = stringResource(R.string.quality),
                        value = review.qualityRating
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    RatingBadge(label = stringResource(R.string.venue), value = review.venueRating)
                    RatingBadge(
                        label = stringResource(R.string.value_for_money),
                        value = review.valueForMoneyRating
                    )
                }
            }

            // Would Repeat
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                RatingBadge(
                    label = if (review.wouldRepeat) stringResource(R.string.would_repeat) + "👍"
                    else stringResource(R.string.wouldn_t_repeat) + "👎",
                    value = if (review.wouldRepeat) 1 else 0,
                    isWouldRepeat = true
                )
            }

            // Comment
            if (review.comment.isNotBlank()) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text(
                        text = review.comment,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                        ),
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }

            //Delete Opinion
            if (userRole == "admin" && review.id != null) {
                Button(
                    onClick = { showDialog = true },
                    colors = ButtonDefaults.buttonColors(Red),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.delete_opinion))
                }
            }
        }
    }

    if (showDialog) {
        DeleteConfirmationDialog(
            onConfirm = {
                showDialog = false
                review.id?.let { onDeleteConfirmed(it) }
            },
            onDismiss = { showDialog = false }
        )
    }
}

@Composable
fun RestaurantCard(
    restaurant: Restaurant,
    onClick: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(restaurant.id) },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Restaurant Name
            Text(
                text = restaurant.name,
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            // Average (badge)
            restaurant.averageRating?.let { avg ->
                OverallRatingBadgeCompact(avg)
            } ?: Surface(
                shape = MaterialTheme.shapes.small,
                color = MaterialTheme.colorScheme.surfaceVariant,
                tonalElevation = 2.dp
            ) {
                Text(
                    text = stringResource(R.string.no_ratings_yet),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }

            // Address
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = stringResource(R.string.location),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${restaurant.street} ${restaurant.streetNumber}, ${restaurant.city}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

private fun formatTimestamp(timestamp: com.google.firebase.Timestamp): String {
    val sdf = SimpleDateFormat("MMMM d, yyyy", Locale.getDefault())
    val date = timestamp.toDate()
    return sdf.format(date)
}

@Composable
fun DeleteConfirmationDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(stringResource(R.string.confirmation))
        },
        text = {
            Text(stringResource(R.string.delete_opinion_confirmation_message))
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(stringResource(R.string.delete))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}
