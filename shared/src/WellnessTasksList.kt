package com.example.aip_les_9

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

@Composable
fun WellnessTasksList(
    list: List<WellnessTask>,
    onCheckedTask: (WellnessTask, Boolean) -> Unit,
    onCloseTask: (WellnessTask) -> Unit,
    modifier: Modifier = Modifier,
    state: LazyListState = rememberLazyListState()
) {
    val coroutineScope = rememberCoroutineScope()
    val showButton by remember {
        derivedStateOf {
            state.firstVisibleItemIndex > 0
        }
    }

    Box(modifier = modifier) {
        LazyColumn(
            state = state,
            modifier = Modifier.fillMaxSize()
        ) {
            items(
                items = list,
                key = { task -> task.id }
            ) { task ->
                WellnessTaskItem(
                    taskName = task.label,
                    checked = task.checked,
                    onCheckedChange = { checked -> onCheckedTask(task, checked) },
                    onClose = { onCloseTask(task) }
                )
            }
        }

        LazyColumnScrollbar(
            state = state,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .fillMaxHeight()
        )

        AnimatedVisibility(
            visible = showButton,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp)
        ) {
            FloatingActionButton(
                onClick = {
                    coroutineScope.launch {
                        try {
                            println(">>> [Coroutine] Scroll animation started")
                            state.animateScrollToItem(index = 0)
                            println(">>> [Coroutine] Scroll animation completed successfully")
                        } catch (e: CancellationException) {
                            println(">>> [Coroutine] Animation was CANCELLED because the list left composition!")
                            throw e
                        } finally {
                            println(">>> [Coroutine] Finally block executed / Resources freed")
                        }
                    }
                }
            ) {
                Text(
                    text = "Scroll to top",
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }
    }
}
