package com.fullcreative.citypulse.presentation.components

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.fullcreative.citypulse.domain.model.CityEvent

@Composable
fun CityListContent(
    modifier: Modifier,
    cities: List<CityEvent>,
    selectedCity: CityEvent?,
    onItemClick: (CityEvent) -> Unit
) {
    LazyColumn(modifier = modifier) {
        items(cities) { event ->
            CityListItem(
                event = event,
                isSelected = selectedCity?.id == event.id,
                onClick = { onItemClick(event) }
            )
        }
    }
}
