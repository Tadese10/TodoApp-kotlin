package com.tadese.framework.presentation.common

import androidx.fragment.app.FragmentFactory
import androidx.lifecycle.ViewModelProvider
import com.tadese.framework.presentation.comment.CommentFragment
import com.tadese.framework.presentation.post.PostFragment
import com.tadese.framework.presentation.todo.TodoFragment
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Inject
import javax.inject.Singleton

@ExperimentalCoroutinesApi
@FlowPreview
class AppFragmentFactory
@Inject
constructor(
    private val viewModelFactory: ViewModelProvider.Factory,
): FragmentFactory(){

    override fun instantiate(classLoader: ClassLoader, className: String) =

        when(className){

            TodoFragment::class.java.name -> {
                val fragment = TodoFragment(viewModelFactory)
                fragment
            }

            PostFragment::class.java.name -> {
                val fragment = PostFragment(viewModelFactory)
                fragment
            }

            CommentFragment::class.java.name -> {
                val fragment = CommentFragment(viewModelFactory)
                fragment
            }

            else -> {
                super.instantiate(classLoader, className)
            }
        }
}