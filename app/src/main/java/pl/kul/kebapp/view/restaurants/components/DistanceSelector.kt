package pl.kul.kebapp.view.restaurants.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import pl.kul.kebapp.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DistanceSelector(
    selectedRadius: Int,
    onRadiusChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val options = listOf(1, 2, 3, 5, 10, 15, 20, 25)
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        TextField(
            value = "$selectedRadius km",
            onValueChange = {}, // pole tekstowe jest tylko do wyświetlania - pewnie można to zrobić inaczej ale nwm
            readOnly = true,
            label = { Text(stringResource(R.string.search_radius)) },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Filled.ArrowDropDown,
                    contentDescription = null
                )
            },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text("$option km") },
                    onClick = {
                        onRadiusChange(option)
                        expanded = false
                    }
                )
            }
        }
    }
}