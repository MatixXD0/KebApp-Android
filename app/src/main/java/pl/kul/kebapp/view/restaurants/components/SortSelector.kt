package pl.kul.kebapp.view.restaurants.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import pl.kul.kebapp.R
import pl.kul.kebapp.model.enums.SortOption
import pl.kul.kebapp.model.enums.SortType


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SortDropdown(
    selectedOption: SortOption,
    onOptionSelected: (SortType) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current//Kontekst jest tu potrzebny, żeby prawidłowo wyświetlać tłumaczenia

    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        TextField(
            value = context.getString(selectedOption.type.displayNameResId),
            onValueChange = {},
            readOnly = true,
            label = { Text(stringResource(R.string.sort_by)) },
            trailingIcon = {
                Row {
                    // Ikona kierunku
                    Icon(
                        imageVector = if (selectedOption.ascending)
                            Icons.Default.KeyboardArrowUp
                        else
                            Icons.Default.KeyboardArrowDown,
                        contentDescription = if (selectedOption.ascending)
                            "Ascending"
                        else
                            "Descending"
                    )
                    // Ikona rozwijania
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                }
            },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            SortType.entries.forEach { type ->
                DropdownMenuItem(
                    text = {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(context.getString(type.displayNameResId))

                            if (type == selectedOption.type) {
                                Icon(
                                    imageVector = if (selectedOption.ascending)
                                        Icons.Default.KeyboardArrowUp
                                    else
                                        Icons.Default.KeyboardArrowDown,
                                    contentDescription = null
                                )
                            }
                        }
                    },
                    onClick = {
                        onOptionSelected(type)
                        expanded = false
                    }
                )
            }
        }
    }
}
