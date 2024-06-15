package com.example.timeweaver.roomDB

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface FixedDAO {
    @Insert
    fun insert(item: FixedEntity)

    //(onConflict = REPLACE)
    /*@Query("SELECT * FROM todo WHERE date = :date")
    fun getAllByDate(date: String): LiveData<List<TodoEntity>?>*/

    @Update
    suspend fun update(item: FixedEntity)

    @Delete
    fun delete(item: FixedEntity)

    @Query("SELECT * FROM FixedEntity")
    fun getAll(): LiveData<List<FixedEntity>>

    @Query("SELECT * FROM FixedEntity WHERE itemId = :id")
    suspend fun getEntityById(id: Int): FixedEntity?
}
