package com.example.timeweaver.roomDB

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [TodoEntity::class],
    version = 1,
    exportSchema = false
)
abstract class TodoDatabase : RoomDatabase() {
    abstract fun getDao():TodoDAO


    companion object {
        private var instance: TodoDatabase? = null
        private var todoDAO: TodoDAO? = null

        private var database: TodoDatabase? = null
        fun getItemDatabase(context: Context): TodoDatabase {
            return database ?: Room.databaseBuilder(    //만약 database 값이 null이면(없으면) database 생성해서 넘겨줌
                context,
                TodoDatabase::class.java,
                "TodoDB"
            ).build()
                .also {
                    database = it
                }
        }

//        fun getInstance(context: Context): TodoDatabase? {
//            if (instance == null) {
//                synchronized(TodoDatabase::class) {
//                    instance = Room.databaseBuilder(
//                        context.applicationContext,
//                        TodoDatabase::class.java,
//                        "TodoDB.db"
//                    )
//                        .build()
//                        .also {
//                            database = it
//                        }
//                }
//            }
//            return instance
//        }
    }
}
