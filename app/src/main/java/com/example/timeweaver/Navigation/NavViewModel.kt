package com.example.timeweaver.Navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.timeweaver.Screens.FixedTask
import com.example.timeweaver.Screens.Task

class NavViewModel: ViewModel(){
    var tasklist = mutableStateListOf<Task>()

    var fixedtasklist = mutableStateListOf<FixedTask>()

    init {
        tasklist.add(Task("Calendar", tasklist.size + 1, 0, false, false, 0, 0))
        tasklist.add(Task("Fixed", tasklist.size + 1, 0, false, false, 0, 0))
        tasklist.add(Task("Schedule", tasklist.size + 1, 0, false, false, 0, 0))
        tasklist.add(Task("My", tasklist.size + 1, 0, false, false,  0, 0))
    }

    fun addtask(name: String, ID: Int, importance: Int, completed: Boolean, once: Boolean, deadline: Int, time: Int) {
        tasklist.add(Task(name, ID, importance, completed, once, deadline, time))
    }

    fun addfixedtask(name: String, ID: Int, day: String, startTime: Int, length: Int) {
        fixedtasklist.add(FixedTask(name, ID, day, startTime, length))
    }
}