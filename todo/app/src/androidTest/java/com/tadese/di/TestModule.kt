package com.tadese.di

import androidx.room.Room
import com.tadese.framework.datasource.cache.database.TodoDatabase
import com.tadese.framework.datasource.data.TestAppDataFactory
import com.tadese.framework.presentation.TestBaseApplication
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Singleton


@ExperimentalCoroutinesApi
@FlowPreview
@Module
object TestModule {

    @JvmStatic
    @Singleton
    @Provides
    fun provideNoteDb(app: TestBaseApplication): TodoDatabase {
        return Room
            .inMemoryDatabaseBuilder(app, TodoDatabase::class.java)
            .fallbackToDestructiveMigration()
            .build()
    }
    @JvmStatic
    @Singleton
    @Provides
    fun provideClassLoader(
        application: TestBaseApplication,
    ) : ClassLoader {
        return application.javaClass.classLoader
    }

    @JvmStatic
    @Singleton
    @Provides
    fun provideTestAppDataFactory(
        application: TestBaseApplication,
        classLoader: ClassLoader
    ) : TestAppDataFactory {
        return TestAppDataFactory(application, classLoader)
    }

}