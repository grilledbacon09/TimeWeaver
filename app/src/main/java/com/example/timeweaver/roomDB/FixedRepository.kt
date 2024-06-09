package com.example.timeweaver.roomDB

import androidx.lifecycle.LiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class FixedRepository(db:FixedDatabase) {
    val FixedDAO = db.fixedDao()

    fun insert(todo: FixedEntity){
        FixedDAO.insert(todo)
    }


    fun getAll(): LiveData<List<FixedEntity>> {
        return FixedDAO.getAll()
    }

    fun delete(todo: FixedEntity){
        GlobalScope.launch(Dispatchers.IO) {
            FixedDAO.delete(todo)
        }
    }
}
