package com.example.timeweaver.roomDB

import androidx.lifecycle.LiveData
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

interface FixedDAO {
    @Insert
    fun insert(todo: FixedEntity)

    //(onConflict = REPLACE)
    /*@Query("SELECT * FROM todo WHERE date = :date")
    fun getAllByDate(date: String): LiveData<List<TodoEntity>?>*/

    @Delete
    fun delete(todo: FixedEntity)

    @Query("SELECT * FROM todo")
    fun getAll(): LiveData<List<FixedEntity>>
}