package com.example.timeweaver.roomDB

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface FixedDAO {
    @Insert
    fun insert(item: FixedEntity)

    //(onConflict = REPLACE)
    /*@Query("SELECT * FROM todo WHERE date = :date")
    fun getAllByDate(date: String): LiveData<List<TodoEntity>?>*/

    @Delete
    fun delete(item: FixedEntity)

    @Query("SELECT * FROM FixedEntity")
    fun getAll(): LiveData<List<FixedEntity>>
}