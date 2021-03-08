package com.tadese.framework.presentation.post

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.tadese.R
import com.tadese.framework.presentation.common.BaseFragment

class PostFragment
constructor(
    private val viewModelFactory: ViewModelProvider.Factory
): BaseFragment(R.layout.fragment_post, true) {

    override fun setTitle() = "Posts"

    override fun inject() {
        getAppComponent().inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
}