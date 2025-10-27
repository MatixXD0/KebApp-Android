package pl.kul.kebapp.view.restaurants.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import pl.kul.kebapp.R
import pl.kul.kebapp.ui.theme.Bad
import pl.kul.kebapp.ui.theme.Good
import pl.kul.kebapp.ui.theme.Medium

@Composable
fun OverallRatingBadge(overallRating: Int) {
    Surface(
        shape = MaterialTheme.shapes.medium,
        color = getRatingColor(overallRating.toDouble()),
        tonalElevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "$overallRating/10",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.Center) {
                for (i in 1..10) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = stringResource(R.string.star),
                        tint = if (i <= overallRating) Color.White else Color.Black
                    )
                }
            }
        }
    }
}

@Composable
fun OverallRatingBadge(overallRating: Double) {
    Surface(
        shape = MaterialTheme.shapes.medium,
        color = getRatingColor(overallRating),
        tonalElevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "${"%.1f".format(overallRating)}/10",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.Center) {
                for (i in 1..10) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = stringResource(R.string.star),
                        tint = if (i <= overallRating.toInt()) Color.White else Color.Black
                    )
                }
            }
        }
    }
}

@Composable
fun OverallRatingBadgeCompact(overallRating: Double) {
    Surface(
        shape = MaterialTheme.shapes.small,
        color = getRatingColor(overallRating),
        tonalElevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "${"%.1f".format(overallRating)}/10",
                style = MaterialTheme.typography.labelLarge,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(horizontalArrangement = Arrangement.Center) {
                for (i in 1..10) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = stringResource(R.string.star),
                        tint = if (i <= overallRating.toInt()) Color.White else Color.Black,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun RatingBadge(label: String, value: Double) {
    Surface(
        shape = MaterialTheme.shapes.small,
        color = getRatingColor(value),
        tonalElevation = 2.dp
    ) {
        Text(
            text = "$label: ${"%.1f".format(value)}/10",
            style = MaterialTheme.typography.labelLarge,
            color = Color.White,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}

@Composable
fun RatingBadge(label: String, value: Int, isWouldRepeat: Boolean = false) {
    Surface(
        shape = MaterialTheme.shapes.small,
        color = if (isWouldRepeat) {
            if (value == 1) Color(0xFF2E7D32) else Color(0xFFD32F2F)
        } else {
            getRatingColor(value.toDouble())
        },
        tonalElevation = 2.dp
    ) {
        Text(
            text = if (isWouldRepeat) {
                if (value == 1) "👍 $label" else "👎 $label"
            } else {
                "$label: $value/10"
            },
            style = MaterialTheme.typography.labelLarge,
            color = Color.White,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun PercentageBadge(label: String, percentage: Double) {
    Surface(
        shape = MaterialTheme.shapes.small,
        color = getPercentageColor(percentage),
        tonalElevation = 2.dp
    ) {
        Text(
            text = "$label: ${"%.0f".format(percentage)}%",
            style = MaterialTheme.typography.labelLarge,
            color = Color.White,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}

private fun getPercentageColor(percentage: Double): Color {
    return when {
        percentage >= 75.0 -> Good
        percentage >= 40.0 -> Medium
        else -> Bad
    }
}

fun getRatingColor(rating: Double): Color {
    return when {
        rating >= 7.5 -> Good
        rating >= 4.0 -> Medium
        else -> Bad
    }
}
