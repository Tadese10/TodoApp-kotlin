package com.tadese.di

import android.content.SharedPreferences
import androidx.lifecycle.ViewModelProvider
import com.tadese.business.interactors.authentication.AuthenticationInteractors
import com.tadese.business.interactors.comment.CommentInteractors
import com.tadese.business.interactors.common.CommonInteractors
import com.tadese.business.interactors.common.GetSavedUserData
import com.tadese.business.interactors.post.PostInteractors
import com.tadese.business.interactors.todo.TodoInteractors
import com.tadese.framework.presentation.common.AppViewModelFactory
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Singleton

@ExperimentalCoroutinesApi
@FlowPreview
@Module
object AppViewModelModule {

    @Singleton
    @JvmStatic
    @Provides
    fun provideAppViewModelFactory(
        authenticationInteractors: AuthenticationInteractors,
        commentInteractors: CommentInteractors,
        postInteractors: PostInteractors,
        todoInteractors: TodoInteractors,
        commonInteractors: CommonInteractors,
        editor: SharedPreferences.Editor,
        sharedPreferences: SharedPreferences
    ): ViewModelProvider.Factory{
        return AppViewModelFactory(
            authenticationInteractors = authenticationInteractors,
            postInteractors = postInteractors,
            commentInteractors = commentInteractors,
            todoInteractors = todoInteractors,
            commonInteractors = commonInteractors,
            editor = editor,
            sharedPreferences = sharedPreferences
        )
    }

}