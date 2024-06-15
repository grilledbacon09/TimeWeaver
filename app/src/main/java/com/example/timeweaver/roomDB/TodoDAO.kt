package com.example.timeweaver.roomDB

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import java.nio.charset.CodingErrorAction.REPLACE

@Dao
interface TodoDAO {
    @Insert
    fun insert(todo: TodoEntity)

    @Update
    suspend fun update(todo: TodoEntity)


    @Delete
    fun delete(todo: TodoEntity)

    @Query("SELECT * FROM todo")
    fun getAll(): LiveData<List<TodoEntity>>


}
