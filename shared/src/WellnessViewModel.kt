package com.example.aip_les_9

import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel

class WellnessViewModel : ViewModel() {
    val tasks: List<WellnessTask>
        field = getWellnessTasks().toMutableStateList()

    fun remove(item: WellnessTask) {
        tasks.remove(item)
    }

    fun changeTaskChecked(item: WellnessTask, checked: Boolean) {
        tasks.find { it.id == item.id }?.let { task ->
            task.checked = checked
        }
    }
}

private fun getWellnessTasks() = List(1000) { i -> WellnessTask(i, "Task # $i") }
