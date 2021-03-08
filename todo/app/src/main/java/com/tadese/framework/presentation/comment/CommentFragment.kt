package com.tadese.framework.presentation.comment

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.tadese.R
import com.tadese.framework.presentation.common.BaseFragment

class CommentFragment
constructor(
    private val viewModelFactory: ViewModelProvider.Factory
): BaseFragment(R.layout.fragment_comment, true) {

    override fun setTitle() = "Post Comment"

    override fun inject() {
      getAppComponent().inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
}