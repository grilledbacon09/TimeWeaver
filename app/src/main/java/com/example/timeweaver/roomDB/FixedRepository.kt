package com.example.timeweaver.roomDB

import androidx.lifecycle.LiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import androidx.lifecycle.map

class FixedRepository(db: FixedDatabase) {
    val FixedDAO = db.fixedDao()

    suspend fun insert(fixed: FixedEntity){
        FixedDAO.insert(fixed)
    }

    suspend fun update(fixed: FixedEntity){
        FixedDAO.update(fixed)
    }

    fun getAll(): LiveData<List<FixedEntity>> {
        return FixedDAO.getAll().map { fixedEntities ->
            fixedEntities.map { entity ->
                FixedEntity(entity.name, entity.startH, entity.days, entity.duration)
            }
        }
    }

    fun delete(fixed: FixedEntity){
        GlobalScope.launch(Dispatchers.IO) {
            FixedDAO.delete(fixed)
        }
    }

    suspend fun getEntityById(id: Int): FixedEntity? {
        return FixedDAO.getEntityById(id)
    }

    fun deleteAll(){
        FixedDAO.deleteAll()
    }
}
