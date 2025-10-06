package com.example.krishna_project.di

import android.content.Context
import androidx.room.Room
import com.example.krishna_project.data.local.AppDatabase
import com.example.krishna_project.data.local.PostDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "krishna_project.db"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    @Singleton
    fun providePostDao(appDatabase: AppDatabase): PostDao {
        return appDatabase.postDao()
    }

    @Provides
    @Singleton
    fun provideUserDao(appDatabase: AppDatabase): com.example.krishna_project.data.local.UserDao {
        return appDatabase.userDao()
    }
}
