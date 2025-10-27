package pl.kul.kebapp.model.enums

import pl.kul.kebapp.R

enum class SortType(val displayNameResId: Int) {
    NAME(R.string.sort_name),
    RATING(R.string.sort_rating),
    DISTANCE(R.string.sort_distance)
}


data class SortOption(
    val type: SortType,
    val ascending: Boolean = true
)