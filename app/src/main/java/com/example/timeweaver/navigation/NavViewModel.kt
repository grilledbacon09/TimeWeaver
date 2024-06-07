package com.example.timeweaver.navigation

import android.icu.text.SimpleDateFormat
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.timeweaver.screens.FixedTask
import com.example.timeweaver.screens.Task
import com.example.timeweaver.screens.Freetime
import com.example.timeweaver.screens.ScheduledTask
import java.util.Date
import java.util.Locale

class NavViewModel: ViewModel(){

    val fixedTaskArray = Array(24){ Array(7) { "" } }

    var tasklist = mutableStateListOf<Task>()

    var fixedtasklist = mutableStateListOf<FixedTask>()

    var scheduledtasklist = mutableStateListOf<ScheduledTask>()

    var scheduleName = mutableStateOf("")
    var estimatedTimeH = mutableStateOf("")
    var estimatedTimeM = mutableStateOf("")
    var once = mutableStateOf(false)
    var importance = mutableStateOf("")

    // Define date-related properties
    val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.KOREA)
    val date = Date() // current date

    var yearState = mutableStateOf(formatter.format(date).split("-")[0])
    var monthState = mutableStateOf(formatter.format(date).split("-")[1])
    var dayState = mutableStateOf(formatter.format(date).split("-")[2])

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

    //CalenderPlus 이용 함수
    fun changeTaskChecked() {
        once.value = !once.value
    }





    fun createSchedule(){
        var freetimelist = mutableListOf<Freetime>()

        var daylist = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
        for (day in daylist) {
            freetimelist.add(Freetime(day, 0, 1440))
        }
        for (fixedtask in fixedtasklist){
            var flag: Boolean = false
            for (freetime in freetimelist){
                if (fixedtask.day == freetime.day && fixedtask.startTime == freetime.startTime){
                    freetime.startTime += fixedtask.length
                    freetime.length -= fixedtask.length
                    if (freetime.length == 0)
                        freetimelist.remove(freetime)

                    flag = true
                }
            }
            if (flag == false){
                for (freetime in freetimelist){
                    if(freetime.startTime < fixedtask.startTime && fixedtask.startTime < freetime.startTime + freetime.length){
                        freetime.length = fixedtask.startTime - freetime.startTime
                        if (freetime.length == 0)
                            freetimelist.remove(freetime)

                        freetimelist.add(
                            Freetime(freetime.day,
                            fixedtask.startTime + fixedtask.length,
                            (freetime.startTime + freetime.length) - (fixedtask.startTime + fixedtask.length))
                        )
                        break
                    }
                }
            }
        }

        tasklist.sortBy { it.importance }

        var importance_sum = 0
        var total_freetime = 0

        for (task in tasklist){
            importance_sum += task.importance
        }
        for (freetime in freetimelist){
            total_freetime += freetime.length
        }
        val weekly_freetime = total_freetime
        var scheduledtaskID = 0

        for (task in tasklist){
            if (!task.completed){
                if (task.importance >= 80){                                             //Task must be finished this week
                    if (task.once){                                                     //Must finish task at once
                        for (freetime in freetimelist){
                            if (task.time <= freetime.length){
                                scheduledtasklist.add(ScheduledTask(task.name, scheduledtaskID++, freetime.day, freetime.startTime, task.time))

                                freetime.startTime += task.time
                                freetime.length -= task.time
                                if (freetime.length == 0)
                                    freetimelist.remove(freetime)

                                total_freetime -= task.time

                                task.time = 0
                                task.completed = true
                                break
                            }
                        }
                        continue
                    }

                    if (task.once == false){                                            //Do not need to, but still finish task at once for convenience
                        for (freetime in freetimelist){
                            if (task.time <= freetime.length){
                                scheduledtasklist.add(ScheduledTask(task.name, scheduledtaskID++, freetime.day, freetime.startTime, task.time))

                                freetime.startTime += task.time
                                freetime.length -= task.time
                                if (freetime.length == 0)
                                    freetimelist.remove(freetime)

                                total_freetime -= task.time

                                task.time = 0
                                task.completed = true
                                break
                            }
                        }
                        continue
                    }

                    if (task.time != 0 && task.once == false){                          //Task cannot be finished at once, so divide into multiple sessions
                        var time_sum = 0
                        for (freetime in freetimelist){
                            if (freetime.length == 0)
                                continue

                            time_sum += freetime.length
                            if (time_sum >= task.time){
                                scheduledtasklist.add(ScheduledTask(task.name, scheduledtaskID++, freetime.day, freetime.startTime, task.time))

                                freetime.length -= task.time
                                if (freetime.length == 0)
                                    freetimelist.remove(freetime)

                                total_freetime -= task.time

                                task.time = 0
                                task.completed = true
                                break
                            }
                            else{
                                scheduledtasklist.add(ScheduledTask(task.name, scheduledtaskID++, freetime.day, freetime.startTime, freetime.length))

                                task.time -= freetime.length
                                total_freetime -= freetime.length

                                freetime.length = 0
                                freetimelist.remove(freetime)
                            }

                        }
                    }
                }


                else if (total_freetime != 0){
                    if (task.time > weekly_freetime && task.importance >= 60){          //Task is due next week and cannot finish in one week
                        var time_this_week = Math.min(total_freetime, (task.time - weekly_freetime) * 2)

                        if (task.once == false){
                            for (freetime in freetimelist){
                                if (time_this_week <= freetime.length){
                                    scheduledtasklist.add(ScheduledTask(task.name, scheduledtaskID++, freetime.day, freetime.startTime, time_this_week))

                                    freetime.startTime += time_this_week
                                    freetime.length -= time_this_week
                                    if (freetime.length == 0)
                                        freetimelist.remove(freetime)

                                    total_freetime -= time_this_week

                                    task.time -= time_this_week
                                    time_this_week = 0
                                    break
                                }
                            }
                            continue
                        }

                        if (time_this_week != 0 && task.once == false){
                            var time_sum = 0
                            for (freetime in freetimelist){
                                if (freetime.length == 0)
                                    continue

                                time_sum += freetime.length
                                if (time_sum >= time_this_week){
                                    scheduledtasklist.add(ScheduledTask(task.name, scheduledtaskID++, freetime.day, freetime.startTime, time_this_week))

                                    freetime.length -= task.time
                                    if (freetime.length == 0)
                                        freetimelist.remove(freetime)

                                    total_freetime -= task.time

                                    task.time -= time_this_week
                                    time_this_week = 0
                                    break
                                }
                                else{
                                    scheduledtasklist.add(ScheduledTask(task.name, scheduledtaskID++, freetime.day, freetime.startTime, freetime.length))

                                    time_this_week -= freetime.length
                                    total_freetime -= freetime.length
                                    task.time -= time_this_week

                                    freetime.length = 0
                                    freetimelist.remove(freetime)
                                }
                            }
                        }
                    }

                    else{                                                                   //If you have enough time
                        if (task.once == false){
                            val importance_weight: Float = (task.importance / importance_sum).toFloat()
                            var time_this_week = Math.min(total_freetime, (Math.round(task.time * importance_weight) / 30) * 30)

                            if (time_this_week == 0)
                                continue

                            for (freetime in freetimelist){
                                if (time_this_week <= freetime.length){
                                    scheduledtasklist.add(ScheduledTask(task.name, scheduledtaskID++, freetime.day, freetime.startTime, time_this_week))

                                    freetime.startTime += time_this_week
                                    freetime.length -= time_this_week
                                    if (freetime.length == 0)
                                        freetimelist.remove(freetime)

                                    total_freetime -= time_this_week

                                    task.time -= time_this_week
                                    time_this_week = 0
                                    break
                                }
                            }

                            if(time_this_week != 0){
                                var time_sum = 0
                                for (freetime in freetimelist){
                                    if (freetime.length == 0)
                                        continue

                                    time_sum += freetime.length
                                    if (time_sum >= time_this_week){
                                        scheduledtasklist.add(ScheduledTask(task.name, scheduledtaskID++, freetime.day, freetime.startTime, time_this_week))

                                        freetime.length -= time_this_week
                                        if (freetime.length == 0)
                                            freetimelist.remove(freetime)

                                        total_freetime -= time_this_week

                                        task.time -= time_this_week
                                        time_this_week = 0
                                        break
                                    }
                                    else{
                                        scheduledtasklist.add(ScheduledTask(task.name, scheduledtaskID++, freetime.day, freetime.startTime, freetime.length))

                                        time_this_week -= freetime.length
                                        total_freetime -= freetime.length
                                        task.time -= time_this_week

                                        freetime.length = 0
                                        freetimelist.remove(freetime)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}