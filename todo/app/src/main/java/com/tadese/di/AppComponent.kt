package com.tadese.di

import com.tadese.framework.presentation.BaseApplication
import com.tadese.framework.presentation.MainActivity
import com.tadese.framework.presentation.authentication.AuthenticationActivity
import com.tadese.framework.presentation.comment.CommentFragment
import com.tadese.framework.presentation.post.PostFragment
import com.tadese.framework.presentation.todo.TodoFragment
import dagger.BindsInstance
import dagger.Component
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Singleton


@ExperimentalCoroutinesApi
@FlowPreview
@Singleton
@Component(
    modules = [
        AppModule::class,
        ProductionModule::class,
        AppViewModelModule::class,
        AppFragmentFactoryModule::class
    ]
)
interface AppComponent {

    @Component.Factory
    interface Factory{
        fun create(@BindsInstance app: BaseApplication) : AppComponent
    }

    fun inject(authenticationActivity: AuthenticationActivity)

    fun inject(mainActivity: MainActivity)
    //MainActivity Fragments
    fun inject(todoFragment: TodoFragment)
    fun inject(postFragment: PostFragment)
    fun inject(commentFragment: CommentFragment)

}