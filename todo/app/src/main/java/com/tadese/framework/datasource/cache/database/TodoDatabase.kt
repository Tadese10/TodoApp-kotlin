package com.tadese.framework.datasource.cache.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.tadese.framework.datasource.cache.database.TodoDatabase.Companion.db_version
import com.tadese.framework.datasource.cache.model.PostEntity
import com.tadese.framework.datasource.cache.model.TodoEntity
import com.tadese.framework.datasource.cache.model.UsersEntity


@Database(entities = [PostEntity::class, TodoEntity::class, UsersEntity::class], version = db_version, exportSchema = true)
abstract class TodoDatabase : RoomDatabase() {

    abstract fun todoDao(): TodoDao

    companion object{
        const val db_version = 1
        const val DATABASE_NAME = "todo_db"
    }
}