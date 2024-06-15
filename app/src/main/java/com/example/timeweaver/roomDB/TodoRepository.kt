package com.example.timeweaver.roomDB

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class TodoRepository (db:TodoDatabase){
     val todoDAO = db.getDao()



    suspend fun insert(todo: TodoEntity){
        todoDAO.insert(todo)
    }

    suspend fun update(todo: TodoEntity){
        todoDAO.update(todo)
    }

    fun getAll(): LiveData<List<TodoEntity>> {
        return todoDAO.getAll().map { todoEntities ->
            todoEntities.map { entity ->
                TodoEntity(entity.name, entity.id, entity.importance,
                    entity.completed, entity.once, entity.deadline,
                    entity.timeH)
            }
        }
    }


    fun delete(todo: TodoEntity){
        GlobalScope.launch(Dispatchers.IO) {
            todoDAO.delete(todo)
        }
    }
}
