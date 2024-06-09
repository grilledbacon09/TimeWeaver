package com.example.timeweaver.roomDB

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

abstract class FixedDatabase : RoomDatabase(){
    abstract fun getDao():FixedDAO


    companion object {
        private var instance: FixedDatabase? = null
        private var todoDAO: FixedDAO? = null

        private var database: FixedDatabase? = null
        fun getItemDatabase(context: Context): FixedDatabase {
            return database
                ?: Room.databaseBuilder(    //만약 database 값이 null이면(없으면) database 생성해서 넘겨줌
                    context,
                    FixedDatabase::class.java,
                    "FixedDB"
                ).build()
                    .also {
                        {
                            database = it
                        }
                    }
        }
    }
}