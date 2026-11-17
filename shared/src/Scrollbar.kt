package com.example.aip_les_9

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun LazyColumnScrollbar(
    state: LazyListState,
    modifier: Modifier = Modifier
)
