package com.tadese.di

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.tadese.framework.datasource.cache.database.TodoDatabase
import com.tadese.framework.datasource.preferences.PreferenceKeys
import com.tadese.framework.presentation.BaseApplication
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Singleton

@ExperimentalCoroutinesApi
@FlowPreview
@Module
object ProductionModule {

    @JvmStatic
    @Singleton
    @Provides
    fun provideAppDb(app: BaseApplication): TodoDatabase {
        return Room
            .databaseBuilder(app, TodoDatabase::class.java, TodoDatabase.DATABASE_NAME)
            .fallbackToDestructiveMigration()
            .build()
    }

    @JvmStatic
    @Singleton
    @Provides
    fun provideSharedPreferences(
        application: BaseApplication
    ): SharedPreferences {
        return application.getSharedPreferences(
            PreferenceKeys.TODO_PREFERENCES,
            Context.MODE_PRIVATE
        )
    }

}