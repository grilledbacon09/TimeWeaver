package com.example.timeweaver.navigation

import android.app.Application
import android.icu.text.SimpleDateFormat
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
//import androidx.lifetaskecycle.viewModelScope
import androidx.lifecycle.viewModelScope
import com.example.timeweaver.roomDB.FixedDatabase
import com.example.timeweaver.roomDB.FixedEntity
import com.example.timeweaver.roomDB.FixedRepository
import com.example.timeweaver.roomDB.TodoDatabase
import com.example.timeweaver.roomDB.TodoEntity
import com.example.timeweaver.roomDB.TodoRepository
import com.example.timeweaver.screens.FixedTask
import com.example.timeweaver.screens.Task
import com.example.timeweaver.screens.Freetime
import com.example.timeweaver.screens.ScheduledTask
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.Calendar
import java.util.Date
import java.util.Locale

class NavViewModel(application: Application) : AndroidViewModel(application) {

    val todoRepository: TodoRepository
    val fixedRepository: FixedRepository

    var tasklist = mutableListOf<Task>()

    var fixedtasklist = mutableListOf<FixedTask>()

    var scheduledtasklist = mutableListOf<ScheduledTask>()

    var freetimelist = mutableListOf<Freetime>()

    init {
        val db = TodoDatabase.getItemDatabase(application.applicationContext)
        todoRepository = TodoRepository(db)
        Log.d("DatabaseLog", "TodoDatabase instance: $db")
        fixedRepository = FixedRepository(FixedDatabase.getFixedDatabase(application))

        // 데이터베이스에서 데이터를 로드한 후에 순차적으로 처리하기 위해 viewModelScope를 사용
        viewModelScope.launch {
            loadTasksFromDatabase()
        }
    }

    // Function to update an item
    fun updateItem(item: TodoEntity) {
        viewModelScope.launch {
            todoRepository.update(item)
            //loadTasksFromDatabase() // Reload tasks after update
        }
    }
    private suspend fun loadTasksFromDatabase() {
        // TodoRepository에서 모든 Task 읽어오기
        val tasks = todoRepository.getAll().value
        Log.d("loading task", "loadTasksFromDatabase: $tasks")
        if (tasks != null) {
            for (todoEntity in tasks) {
                tasklist.add(todoEntity.toTask())
                Log.d("printing tasklist", "loadTasksFromDatabase: $tasklist")
            }

            logDatabaseContents()
            // 데이터베이스 로딩 후 tasklist 내용 로그 출력
            //delay(1000) // 1000밀리초(1초) 기다림
            logTaskListContents()
        }


//        // FixedRepository에서 모든 FixedTask 읽어오기
//        val fixedtasks = todoRepository.getAll().value
//        fixedtasklist.addAll(fixedtasks.map { it.toFixedTask() })
//    }

    // TodoEntity를 Task로 변환하는 확장 함수
//    fun TodoEntity.toTask(): Task {
//        return Task(
//            name = this.name,
//            ID = this.id,
//            once = this.once,
//            importance = this.importance,
//            completed = this.completed,
//            deadline = this.deadline,
//            time = this.timeH
//        )
//    }

    // FixedEntity를 FixedTask로 변환하는 확장 함수
//    fun FixedEntity.toFixedTask(): FixedTask {
//        return FixedTask(
//            ID = this.itemId,
//            name = this.name,
//            startHour = this.startH,
//            days = this.days,
//            duration = this.duration
//        )
//    }

//    // 데이터베이스에서 데이터를 로드하여 tasklist에 추가하는 함수
//    private suspend fun loadTasksFromDatabase() {
//        Log.d("starting loading", "starting loading db")
//        val todoList = todoRepository.todoDAO.getAll().value ?: emptyList()
//        val fixedList = fixedRepository.FixedDAO.getAll().value ?: emptyList()
//
//        if(tasklist!=null)
//        {
//            tasklist.clear()
//        }
//        for ((index, todo) in todoList.withIndex()) {
//            tasklist.add(Task(todo.name, todo.id, todo.importance, todo.completed, todo.once, todo.deadline, todo.timeH))
//            Log.d("PushTaskListLog", "Pushing Tasklist contents: $todo")
//
//            // 마지막 할 일 항목인 경우 로그 출력
//            if (index == todoList.size - 1) {
//                Log.d("PushTaskListLog", "All todo items have been added to tasklist.")
//            }
//        }
//        for (fixed in fixedList) {
//            fixedtasklist.add(FixedTask(fixed.name, fixed.itemId, fixed.days, fixed.startH, fixed.duration))
//        }

//        logDatabaseContents()
//        // 데이터베이스 로딩 후 tasklist 내용 로그 출력
//
//        logTaskListContents()
    }

    // tasklist와 fixedtasklist의 내용을 로그로 출력하는 함수
    fun logTaskListContents() {
        Log.d("TaskListLog", "Tasklist contents: $tasklist")
        Log.d("TaskListLog", "FixedTasklist contents: $fixedtasklist")
    }

    fun logDatabaseContents() {
        val dao = todoRepository.todoDAO
        dao.getAll().observeForever { todoList ->
            Log.d("DatabaseLog", "Todo items: $todoList")
        }

        val fixedDao = fixedRepository.FixedDAO
        fixedDao.getAll().observeForever { fixedList ->
            Log.d("DatabaseLog", "Fixed items: $fixedList")
        }
    }



    val fixedTaskArray = Array(24){ Array(7) { "" } }
    var scheduleArray = Array(24) { Array(7) { "" } }



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

//    init {
//        tasklist.add(Task("Calendar", tasklist.size + 1, 0, false, false, 0, 0))
//        tasklist.add(Task("Fixed", tasklist.size + 1, 0, false, false, 0, 0))
//        tasklist.add(Task("Schedule", tasklist.size + 1, 0, false, false, 0, 0))
//        tasklist.add(Task("My", tasklist.size + 1, 0, false, false,  0, 0))
//    }

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



    val dayMap = mapOf(
        "Sun" to 0,
        "Mon" to 1,
        "Tue" to 2,
        "Wed" to 3,
        "Thu" to 4,
        "Fri" to 5,
        "Sat" to 6
    )

    fun calculateDeadline(task: Task): Int{
        val month_days = intArrayOf(31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)

        val calendar = Calendar.getInstance()
        val current_year = calendar.get(Calendar.YEAR)
        val current_month = calendar.get(Calendar.MONTH)
        val current_day = calendar.get(Calendar.DAY_OF_MONTH)

        val deadline_year: Int = task.deadline / 10000
        val deadline_month: Int = (task.deadline / 100).toInt() - (deadline_year * 100)
        val deadline_day: Int = task.deadline - (deadline_month * 100) - (deadline_year * 10000)


        var cyears: Int = current_year
        if (deadline_month <= 2)
            cyears--
        cyears = cyears / 4 - cyears / 100 + cyears / 400
        var current_date = cyears * 365 + current_day
        for (i in 0..current_month){
            current_date += month_days[i]
        }

        var dyears: Int = deadline_year
        if (deadline_month <= 2)
            dyears--
        dyears = dyears / 4 - dyears / 100 + dyears / 400
        var deadline_date = dyears * 365 + deadline_day

        for (i in 0..deadline_month - 1){
            deadline_date += month_days[i]
        }

        if (deadline_date < current_date)
            return -1
        else
            return deadline_date - current_date
    }

    fun updateImportance(){
        for (task in tasklist){
            if (!task.completed){
                val days_deadline = calculateDeadline(task)
                if (days_deadline == -1) {
                    task.completed = true
                }
                else if (days_deadline <= 7){
                    task.importance = 80 + (task.importance - ((task.importance / 10).toInt() * 10))
                }
                else if (days_deadline <= 14){
                    task.importance = 60 + (task.importance - ((task.importance / 10).toInt() * 10))
                }
                else if (days_deadline <= 21){
                    task.importance = 40 + (task.importance - ((task.importance / 10).toInt() * 10))
                }
                else if (days_deadline < 30) {
                    task.importance = 20 + (task.importance - ((task.importance / 10).toInt() * 10))
                }
                else{
                    continue
                }
            }
        }
    }


    fun createSchedule(){
        //scheduledtasklist.clear()
        //freetimelist.clear()

        var daylist = mutableMapOf(Pair("Sun", 0), Pair("Mon", 0), Pair("Tue", 0), Pair("Wed", 0), Pair("Thu", 0), Pair("Fri", 0), Pair("Sat", 0))
        for (day in daylist) {
            freetimelist.add(Freetime(day.key, 0, 24))
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

                        freetimelist.add(Freetime(freetime.day,
                            fixedtask.startTime + fixedtask.length,
                            (freetime.startTime + freetime.length) - (fixedtask.startTime + fixedtask.length)))
                        break
                    }
                }
            }
        }

        tasklist.sortBy { it.importance }
        tasklist.reverse()

        var importance_sum = 0
        var total_freetime = 0
        var cnt_freetime = 0

        for (task in tasklist){
            importance_sum += task.importance
        }
        for (freetime in freetimelist){
            total_freetime += freetime.length
            cnt_freetime++
        }
        val weekly_freetime = total_freetime
        var scheduledtaskID = 0

        for (task in tasklist){
            if (!task.completed){

                daylist = daylist.toList().sortedBy { it.second }.toMap().toMutableMap()
                val day: String = daylist.toList().first().first
                var dflag = false

                for (freetime in freetimelist){

                    if (freetime.day == day) {

                        if (task.importance >= 80) {

                            if (task.once) {
                                if (task.time <= freetime.length) {
                                    scheduledtasklist.add(ScheduledTask(task.name,scheduledtaskID++,freetime.day,freetime.startTime,task.time))
                                    val day_task_num = daylist.get(day)
                                    var task_num = 0
                                    day_task_num?.let { task_num = day_task_num + 1 }
                                        ?: let { task_num = 0 };
                                    daylist.set(day, task_num)

                                    freetime.startTime += task.time
                                    freetime.length -= task.time
                                    if (freetime.length == 0)
                                        freetimelist.remove(freetime)

                                    total_freetime -= task.time

                                    dflag = true
                                    break
                                }
                            }

                            if (task.once == false && task.time <= freetime.length) {

                                scheduledtasklist.add(ScheduledTask(task.name,scheduledtaskID++,freetime.day,freetime.startTime,task.time))
                                val day_task_num = daylist.get(day)
                                var task_num = 0
                                day_task_num?.let { task_num = day_task_num + 1 }
                                    ?: let { task_num = 0 };
                                daylist.set(day, task_num)

                                freetime.startTime += task.time
                                freetime.length -= task.time
                                if (freetime.length == 0)
                                    freetimelist.remove(freetime)

                                total_freetime -= task.time

                                dflag = true
                                break
                            }
                        }

                        else if (task.importance >= 60 && task.once == false) {
                            val time_this_week = Math.min(total_freetime, (task.time / 2 / 60) * 60)

                            if (time_this_week == 0)
                                break

                            if (time_this_week <= freetime.length){
                                scheduledtasklist.add(ScheduledTask(task.name, scheduledtaskID++, freetime.day, freetime.startTime, time_this_week))
                                val day_task_num = daylist.get(day)
                                var task_num = 0
                                day_task_num?.let { task_num = day_task_num + 1 } ?:let { task_num = 0 };
                                daylist.set(day, task_num)

                                freetime.startTime += time_this_week
                                freetime.length -= time_this_week
                                if (freetime.length == 0)
                                    freetimelist.remove(freetime)

                                total_freetime -= time_this_week

                                dflag = true
                                break
                            }
                        }

                        else if (task.once == false){
                            val importance_weight: Float = (task.importance / importance_sum).toFloat()
                            val time_this_week = Math.min(total_freetime, Math.round(task.time * importance_weight))

                            if (time_this_week == 0)
                                break

                            scheduledtasklist.add(ScheduledTask(task.name, scheduledtaskID++, freetime.day, freetime.startTime, time_this_week))
                            val day_task_num = daylist.get(day)
                            var task_num = 0
                            day_task_num?.let { task_num = day_task_num + 1 } ?:let { task_num = 0 };
                            daylist.set(day, task_num)

                            freetime.startTime += time_this_week
                            freetime.length -= time_this_week
                            if (freetime.length == 0)
                                freetimelist.remove(freetime)

                            total_freetime -= time_this_week

                            dflag = true
                            break
                        }
                    }
                }

                if (dflag == true)
                    continue


                if (task.importance >= 80){                                             //Task must be finished this week
                    if (task.once){                                                     //Must finish task at once
                        for (freetime in freetimelist){
                            if (task.time <= freetime.length){
                                scheduledtasklist.add(ScheduledTask(task.name, scheduledtaskID++, freetime.day, freetime.startTime, task.time))
                                val day_task_num = daylist.get(freetime.day)
                                var task_num = 0
                                day_task_num?.let { task_num = day_task_num + 1 } ?:let { task_num = 0 };
                                daylist.set(freetime.day, task_num)

                                freetime.startTime += task.time
                                freetime.length -= task.time
                                if (freetime.length == 0)
                                    freetimelist.remove(freetime)

                                total_freetime -= task.time

                                break
                            }
                        }
                        continue
                    }

                    if (task.once == false){                                            //Do not need to, but still finish task at once for convenience
                        for (freetime in freetimelist){
                            if (task.time <= freetime.length){
                                scheduledtasklist.add(ScheduledTask(task.name, scheduledtaskID++, freetime.day, freetime.startTime, task.time))
                                val day_task_num = daylist.get(freetime.day)
                                var task_num = 0
                                day_task_num?.let { task_num = day_task_num + 1 } ?:let { task_num = 0 };
                                daylist.set(freetime.day, task_num)

                                freetime.startTime += task.time
                                freetime.length -= task.time
                                if (freetime.length == 0)
                                    freetimelist.remove(freetime)

                                total_freetime -= task.time

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
                                val day_task_num = daylist.get(freetime.day)
                                var task_num = 0
                                day_task_num?.let { task_num = day_task_num + 1 } ?:let { task_num = 0 };
                                daylist.set(freetime.day, task_num)

                                freetime.length -= task.time
                                if (freetime.length == 0)
                                    freetimelist.remove(freetime)

                                total_freetime -= task.time

                                break
                            }
                            else{
                                scheduledtasklist.add(ScheduledTask(task.name, scheduledtaskID++, freetime.day, freetime.startTime, freetime.length))
                                val day_task_num = daylist.get(freetime.day)
                                var task_num = 0
                                day_task_num?.let { task_num = day_task_num + 1 } ?:let { task_num = 0 };
                                daylist.set(freetime.day, task_num)

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
                                    val day_task_num = daylist.get(freetime.day)
                                    var task_num = 0
                                    day_task_num?.let { task_num = day_task_num + 1 } ?:let { task_num = 0 };
                                    daylist.set(freetime.day, task_num)

                                    freetime.startTime += time_this_week
                                    freetime.length -= time_this_week
                                    if (freetime.length == 0)
                                        freetimelist.remove(freetime)

                                    total_freetime -= time_this_week

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
                                    val day_task_num = daylist.get(freetime.day)
                                    var task_num = 0
                                    day_task_num?.let { task_num = day_task_num + 1 } ?:let { task_num = 0 };
                                    daylist.set(freetime.day, task_num)

                                    freetime.length -= task.time
                                    if (freetime.length == 0)
                                        freetimelist.remove(freetime)

                                    total_freetime -= task.time

                                    break
                                }
                                else{
                                    scheduledtasklist.add(ScheduledTask(task.name, scheduledtaskID++, freetime.day, freetime.startTime, freetime.length))
                                    val day_task_num = daylist.get(freetime.day)
                                    var task_num = 0
                                    day_task_num?.let { task_num = day_task_num + 1 } ?:let { task_num = 0 };
                                    daylist.set(freetime.day, task_num)

                                    time_this_week -= freetime.length
                                    total_freetime -= freetime.length

                                    freetime.length = 0
                                    freetimelist.remove(freetime)
                                }
                            }
                        }
                    }

                    else if (task.time < weekly_freetime && task.importance >= 60){                                   //If you have enough time
                        if (task.once == false){
                            var time_this_week = Math.min(total_freetime, (task.time / 2))

                            if (time_this_week == 0)
                                continue

                            for (freetime in freetimelist){
                                if (time_this_week <= freetime.length){
                                    scheduledtasklist.add(ScheduledTask(task.name, scheduledtaskID++, freetime.day, freetime.startTime, time_this_week))
                                    val day_task_num = daylist.get(freetime.day)
                                    var task_num = 0
                                    day_task_num?.let { task_num = day_task_num + 1 } ?:let { task_num = 0 };
                                    daylist.set(freetime.day, task_num)

                                    freetime.startTime += time_this_week
                                    freetime.length -= time_this_week
                                    if (freetime.length == 0)
                                        freetimelist.remove(freetime)

                                    total_freetime -= time_this_week

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
                                        val day_task_num = daylist.get(freetime.day)
                                        var task_num = 0
                                        day_task_num?.let { task_num = day_task_num + 1 } ?:let { task_num = 0 };
                                        daylist.set(freetime.day, task_num)

                                        freetime.length -= time_this_week
                                        if (freetime.length == 0)
                                            freetimelist.remove(freetime)

                                        total_freetime -= time_this_week

                                        break
                                    }
                                    else{
                                        scheduledtasklist.add(ScheduledTask(task.name, scheduledtaskID++, freetime.day, freetime.startTime, freetime.length))
                                        val day_task_num = daylist.get(freetime.day)
                                        var task_num = 0
                                        day_task_num?.let { task_num = day_task_num + 1 } ?:let { task_num = 0 };
                                        daylist.set(freetime.day, task_num)

                                        time_this_week -= freetime.length
                                        total_freetime -= freetime.length

                                        freetime.length = 0
                                        freetimelist.remove(freetime)
                                    }
                                }
                            }
                        }
                    }

                    else {                                                                   //If you have enough time
                        if (task.once == false){
                            val importance_weight: Float = (task.importance / importance_sum).toFloat()
                            var time_this_week = Math.min(total_freetime, Math.round(task.time * importance_weight))

                            if (time_this_week == 0)
                                continue

                            for (freetime in freetimelist){
                                if (time_this_week <= freetime.length){
                                    scheduledtasklist.add(ScheduledTask(task.name, scheduledtaskID++, freetime.day, freetime.startTime, time_this_week))
                                    val day_task_num = daylist.get(freetime.day)
                                    var task_num = 0
                                    day_task_num?.let { task_num = day_task_num + 1 } ?:let { task_num = 0 };
                                    daylist.set(freetime.day, task_num)

                                    freetime.startTime += time_this_week
                                    freetime.length -= time_this_week
                                    if (freetime.length == 0)
                                        freetimelist.remove(freetime)

                                    total_freetime -= time_this_week

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
                                        val day_task_num = daylist.get(freetime.day)
                                        var task_num = 0
                                        day_task_num?.let { task_num = day_task_num + 1 } ?:let { task_num = 0 };
                                        daylist.set(freetime.day, task_num)

                                        freetime.length -= time_this_week
                                        if (freetime.length == 0)
                                            freetimelist.remove(freetime)

                                        total_freetime -= time_this_week

                                        break
                                    }
                                    else{
                                        scheduledtasklist.add(ScheduledTask(task.name, scheduledtaskID++, freetime.day, freetime.startTime, freetime.length))
                                        val day_task_num = daylist.get(freetime.day)
                                        var task_num = 0
                                        day_task_num?.let { task_num = day_task_num + 1 } ?:let { task_num = 0 };
                                        daylist.set(freetime.day, task_num)

                                        time_this_week -= freetime.length
                                        total_freetime -= freetime.length

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

    fun fillSchedule(){
        scheduleArray = Array(24) { Array(7) { "" } }

        Log.i("fillschedule", "1")

        scheduledtasklist.forEach {
            Log.i("fillschedule", "3")
            val startTime = it.startTime
            val endTime = startTime + it.length

            for (j: Int in startTime..<endTime) {
                if (j < 23) {
                    scheduleArray[j][dayMap[it.day]!!] = it.name
                } else {
                    if (it.day == "Sat") {
                        scheduleArray[j - 23][0] = it.name
                    } else {
                        scheduleArray[j - 23][dayMap[it.day]?.plus(1)!!] = it.name
                    }
                }
            }
            Log.i("fillschedule", "4")
        }
    }
}

