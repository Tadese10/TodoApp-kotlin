package com.tadese.di

import com.tadese.framework.datasource.cache.TestAppDaoService
import com.tadese.framework.presentation.TestBaseApplication
import dagger.BindsInstance
import dagger.Component
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Singleton


@FlowPreview
@ExperimentalCoroutinesApi
@Singleton
@Component(
    modules = [
        TestModule::class,
        AppModule::class
    ]
)
interface TestAppComponent : AppComponent  {

    @Component.Factory
    interface Factory{

        fun create(@BindsInstance app: TestBaseApplication): TestAppComponent
    }

    fun inject(testAppDaoService: TestAppDaoService)

}