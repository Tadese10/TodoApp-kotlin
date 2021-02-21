package com.tadese.di

import android.content.SharedPreferences
import com.tadese.business.data.cache.abstraction.AppCacheDataSource
import com.tadese.business.data.cache.implementation.AppCacheDataSourceImple
import com.tadese.framework.datasource.cache.abstraction.AppDaoService
import com.tadese.framework.datasource.cache.database.TodoDao
import com.tadese.framework.datasource.cache.database.TodoDatabase
import com.tadese.framework.datasource.cache.implementation.AppDaoServiceImpl
import com.tadese.framework.datasource.cache.model.LoggedInUserCacheMapper
import com.tadese.framework.datasource.cache.model.PostCacheMapper
import com.tadese.framework.datasource.cache.model.TodoCacheMapper
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Singleton

@ExperimentalCoroutinesApi
@FlowPreview
@Module
object AppModule {

    @JvmStatic
    @Singleton
    @Provides
    fun provideSharedPrefsEditor(
        sharedPreferences: SharedPreferences
    ): SharedPreferences.Editor {
        return sharedPreferences.edit()
    }

    @JvmStatic
    @Singleton
    @Provides
    fun provideTodoDAO(todoDatabase: TodoDatabase): TodoDao {
        return todoDatabase.todoDao()
    }

    @JvmStatic
    @Singleton
    @Provides
    fun providePostCacheMapper(): PostCacheMapper {
        return PostCacheMapper()
    }

    @JvmStatic
    @Singleton
    @Provides
    fun provideTodoCacheMapper(): TodoCacheMapper {
        return TodoCacheMapper()
    }

    @JvmStatic
    @Singleton
    @Provides
    fun provideLoggedInUserCacheMapper(): LoggedInUserCacheMapper {
        return LoggedInUserCacheMapper()
    }

    @JvmStatic
    @Singleton
    @Provides
    fun provideTodoDaoService(
        todoDao: TodoDao,
        postCacheMapper: PostCacheMapper,
        todoCacheMapper: TodoCacheMapper,
        loggedInUserCacheMapper: LoggedInUserCacheMapper
    ): AppDaoService {
        return AppDaoServiceImpl(todoDao, postCacheMapper, todoCacheMapper, loggedInUserCacheMapper)
    }

    @JvmStatic
    @Singleton
    @Provides
    fun provideTodoCacheDataSource(
        todoDaoService: AppDaoService
    ): AppCacheDataSource {
        return AppCacheDataSourceImple(todoDaoService)
    }


}