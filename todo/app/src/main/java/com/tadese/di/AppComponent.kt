package com.tadese.di

import com.tadese.framework.presentation.BaseApplication
import com.tadese.framework.presentation.MainActivity
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
        ProductionModule::class
    ]
)
interface AppComponent {

    @Component.Factory
    interface Factory{
        fun create(@BindsInstance app: BaseApplication) : AppComponent
    }

   // fun inject(mainActivity: MainActivity)

}