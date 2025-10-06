package com.example.krishna_project.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Post::class, Comment::class, User::class], version = 3, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun postDao(): PostDao
    abstract fun userDao(): UserDao
}
