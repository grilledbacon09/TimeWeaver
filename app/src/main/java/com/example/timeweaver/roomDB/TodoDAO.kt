package com.example.timeweaver.roomDB

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import java.nio.charset.CodingErrorAction.REPLACE

@Dao
interface TodoDAO {
    @Insert
    fun insert(todo: TodoEntity)

    //(onConflict = REPLACE)
    /*@Query("SELECT * FROM todo WHERE date = :date")
    fun getAllByDate(date: String): LiveData<List<TodoEntity>?>*/

    @Delete
    fun delete(todo: TodoEntity)

    @Query("SELECT * FROM todo")
    fun getAll(): LiveData<List<TodoEntity>>


}