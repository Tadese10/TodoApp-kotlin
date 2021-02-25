package com.tadese.di

import android.content.SharedPreferences
import com.tadese.business.data.cache.abstraction.AppCacheDataSource
import com.tadese.business.data.cache.implementation.AppCacheDataSourceImple
import com.tadese.business.data.network.abstraction.AppNetworkDatasource
import com.tadese.business.data.network.implementation.AppNetworkDatasourceImple
import com.tadese.business.interactors.authentication.AuthenticationInteractors
import com.tadese.business.interactors.authentication.UserLogin
import com.tadese.business.interactors.comment.AddPostComment
import com.tadese.business.interactors.comment.CommentInteractors
import com.tadese.business.interactors.comment.GetPostComment
import com.tadese.business.interactors.common.CommonInteractors
import com.tadese.business.interactors.common.GetSavedUserData
import com.tadese.business.interactors.post.AddNewPost
import com.tadese.business.interactors.post.GetAllPost
import com.tadese.business.interactors.post.PostInteractors
import com.tadese.business.interactors.todo.*
import com.tadese.framework.datasource.cache.abstraction.AppDaoService
import com.tadese.framework.datasource.cache.database.TodoDao
import com.tadese.framework.datasource.cache.database.TodoDatabase
import com.tadese.framework.datasource.cache.implementation.AppDaoServiceImpl
import com.tadese.framework.datasource.cache.model.LoggedInUserCacheMapper
import com.tadese.framework.datasource.cache.model.PostCacheMapper
import com.tadese.framework.datasource.cache.model.TodoCacheMapper
import com.tadese.framework.datasource.network.abstraction.AppNetworkService
import com.tadese.framework.datasource.network.api.AppNetworkServiceApi
import com.tadese.framework.datasource.network.implementation.AppNetworkServiceImple
import com.tadese.framework.datasource.network.util.NetworkRetrofitBuilder
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
    fun provideAppNetworkService(): AppNetworkServiceApi {
        return NetworkRetrofitBuilder.API_SERVICE_API
    }

    @JvmStatic
    @Singleton
    @Provides
    fun provideAppNetworkServiceImple(
        appNetworkServiceApi: AppNetworkServiceApi
    ): AppNetworkService {
        return AppNetworkServiceImple(appNetworkServiceApi)
    }

    @JvmStatic
    @Singleton
    @Provides
    fun provideAppNetworkDataSource(
        appNetworkServiceImple: AppNetworkServiceImple
    ): AppNetworkDatasource {
        return AppNetworkDatasourceImple(appNetworkServiceImple)
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
    fun provideAppCacheDataSource(
        todoDaoService: AppDaoService
    ): AppCacheDataSource {
        return AppCacheDataSourceImple(todoDaoService)
    }


    @JvmStatic
    @Singleton
    @Provides
    fun provideUserLoginInteractors(
         todoNetworkDataSource: AppNetworkDatasource,
         appCacheDataSource: AppCacheDataSource
    ): AuthenticationInteractors {
        return AuthenticationInteractors(
           userLogin = UserLogin(
               todoNetworkDataSource,
               appCacheDataSource
           )
        )
    }

    @JvmStatic
    @Singleton
    @Provides
    fun provideCommentInteractors(
        todoNetworkDataSource: AppNetworkDatasource
    ): CommentInteractors {
        return CommentInteractors(
            addPostComment = AddPostComment(
                todoNetworkDataSource
            ),
            getPostComment = GetPostComment(
                todoNetworkDataSource
            )
        )
    }


    @JvmStatic
    @Singleton
    @Provides
    fun providePostInteractors(
        todoNetworkDataSource: AppNetworkDatasource
    ): PostInteractors {
        return PostInteractors(
            addNewPost = AddNewPost(
                todoNetworkDataSource
            ),
            getAllPost = GetAllPost(
                todoNetworkDataSource
            )
        )
    }

    @JvmStatic
    @Singleton
    @Provides
    fun provideCommonInteractors(
        appCacheDataSource: AppCacheDataSource
    ): CommonInteractors {
        return CommonInteractors(
            getSavedUserData = GetSavedUserData(
                appCacheDataSource
            )
        )
    }

    @JvmStatic
    @Singleton
    @Provides
    fun provideTodoInteractors(
        todoNetworkDataSource: AppNetworkDatasource,
        appCacheDataSource: AppCacheDataSource
    ): TodoInteractors {
        return TodoInteractors(
            addTodoToNetworkAndSaveInCache = AddTodoToNetworkAndSaveInCache(
                todoNetworkDataSource,
                appCacheDataSource
            ),
            getAllTodoOnNetworkByUserId = GetAllTodoOnNetworkByUserId(
                todoNetworkDataSource,
                appCacheDataSource
            ),
            searchTodoListInCache = SearchTodoListInCache(
                appCacheDataSource
            ),
            getAllTodoListInCache = GetAllTodoListInCache(
                appCacheDataSource = appCacheDataSource
            ),
            getAllTodoNumInCache = GetAllTodoNumInCache(
                appCacheDataSource = appCacheDataSource
            ),
            searchTodoListInCacheById = SearchTodoListInCacheById(
                appCacheDataSource = appCacheDataSource
            ),
            getAllTodoNumInCacheWithQuery = GetAllTodoNumInCacheWithQuery(
                appCacheDataSource = appCacheDataSource
            ),
            deleteAllTodoUserInCache = DeleteAllTodoUserInCache(
                appCacheDataSource = appCacheDataSource
            )
        )
    }


}