package pl.kul.kebapp.view.restaurants.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import kotlin.math.roundToInt

@Composable
fun RatingSlider(
    label: String,
    rating: Float,
    onRatingChange: (Float) -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, style = MaterialTheme.typography.labelLarge)
            Text("${rating.toInt()}/10", style = MaterialTheme.typography.bodyLarge)
        }
        Slider(
            value = rating,
            onValueChange = { onRatingChange(it.roundToInt().toFloat()) },
            valueRange = 1f..10f,
            steps = 8
        )
    }
}