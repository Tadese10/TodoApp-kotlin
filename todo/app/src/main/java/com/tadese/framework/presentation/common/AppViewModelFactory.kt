package com.tadese.framework.presentation.common

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.tadese.business.interactors.authentication.AuthenticationInteractors
import com.tadese.business.interactors.comment.CommentInteractors
import com.tadese.business.interactors.common.CommonInteractors
import com.tadese.business.interactors.common.GetSavedUserData
import com.tadese.business.interactors.post.PostInteractors
import com.tadese.business.interactors.todo.TodoInteractors
import com.tadese.framework.presentation.authentication.AuthenticationViewModel
import com.tadese.framework.presentation.todo.TodoViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Inject
import javax.inject.Singleton


@FlowPreview
@ExperimentalCoroutinesApi
@Singleton
class AppViewModelFactory
@Inject
constructor(
    private val authenticationInteractors: AuthenticationInteractors,
    private val postInteractors: PostInteractors,
    private val commentInteractors: CommentInteractors,
    private val todoInteractors: TodoInteractors,
    private val commonInteractors: CommonInteractors,
    private val editor: SharedPreferences.Editor,
    private val sharedPreferences: SharedPreferences
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when(modelClass){

            AuthenticationViewModel::class.java -> {
                AuthenticationViewModel(
                    authenticationInteractors = authenticationInteractors,
                    commonInteractors = commonInteractors
                ) as T
            }


            TodoViewModel::class.java -> {
                TodoViewModel(
                    todoInteractors = todoInteractors,
                    commonInteractors = commonInteractors,
                    editor = editor,
                    sharedPreferences = sharedPreferences
                ) as T
            }

            else -> {
                throw IllegalArgumentException("unknown model class $modelClass")
            }
        }
    }
}
