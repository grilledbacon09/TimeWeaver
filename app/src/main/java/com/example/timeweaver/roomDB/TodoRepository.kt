package com.example.timeweaver.roomDB

import android.app.Application
import androidx.lifecycle.LiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class TodoRepository (db:TodoDatabase){
     val todoDAO = db.getDao()

//    init{
//        var db = TodoDatabase.getInstance(application)
//        todoDAO = db!!.TodoDAO()
//    }

    fun insert(todo: TodoEntity){
        todoDAO.insert(todo)
    }

    /*fun getAllByDate(date: String): LiveData<List<TodoEntity>?> {
        return todoDAO.getAllByDate(date)
    }*/

    fun getAll(): LiveData<List<TodoEntity>> {
        return todoDAO.getAll()
    }

    fun delete(todo: TodoEntity){
        GlobalScope.launch(Dispatchers.IO) {
            todoDAO.delete(todo)
        }
    }
}