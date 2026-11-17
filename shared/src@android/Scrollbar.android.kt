package com.example.aip_les_9

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
actual fun LazyColumnScrollbar(
    state: LazyListState,
    modifier: Modifier
) {
    // Standard Android UI relies on touch fling interactions and does not render a persistent desktop scrollbar.
}
