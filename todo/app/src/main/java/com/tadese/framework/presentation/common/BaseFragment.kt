package com.tadese.framework.presentation.common

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import com.tadese.R
import com.tadese.di.AppComponent
import com.tadese.framework.presentation.BaseApplication
import com.tadese.framework.presentation.MainActivity
import com.tadese.framework.presentation.UIController
import com.tadese.util.TodoCallback
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import java.lang.ClassCastException

@FlowPreview
@ExperimentalCoroutinesApi
abstract class BaseFragment
constructor(
    private @LayoutRes val layoutRes: Int,
    private val setToolbarContent: Boolean? = false
) : Fragment() {

    lateinit var uiController: UIController
    private lateinit var title : String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(layoutRes, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        title = setTitle()
        changeToolBarContent()
        super.onViewCreated(view, savedInstanceState)
    }

    abstract fun setTitle(): String

    private fun changeToolBarContent() {
        setToolbarContent?.let {
            view?.let { v ->
                val view = View.inflate(
                    v.context,
                    R.layout.default_toolbar_content,
                    null
                )
                view.layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT
                )
                var searchView =
                    activity?.toolbar?.findViewById<LinearLayout>(R.id.search_content_container)
                searchView?.removeAllViews()
                view.findViewById<TextView>(R.id.toolbar_text).text = title
                searchView?.addView(view)
            }
        }
    }

    override fun onDetach() {
        super.onDetach()
    }

    abstract fun inject()

    fun displayToolbarTitle(textView: TextView, title: String?, useAnimation: Boolean) {
        if(title != null){
            showToolbarTitle(textView, title, useAnimation)
        }
        else{
            hideToolbarTitle(textView, useAnimation)
        }
    }

    private fun hideToolbarTitle(textView: TextView, animation: Boolean){
        if(animation){
            textView.fadeOut(
                object: TodoCallback {
                    override fun execute() {
                        textView.text = ""
                    }
                }
            )
        }
        else{
            textView.text = ""
            textView.gone()
        }
    }

    private fun showToolbarTitle(
        textView: TextView,
        title: String,
        animation: Boolean
    ){
        textView.text = title
        if(animation){
            textView.fadeIn()
        }
        else{
            textView.visible()
        }
    }

    fun getAppComponent(): AppComponent {
        return activity?.run {
            (application as BaseApplication).appComponent
        }?: throw Exception("AppComponent is null.")
    }

    override fun onAttach(context: Context) {
        inject()
        super.onAttach(context)
        setUIController(null) // null in production
    }

    fun setUIController(mockController: UIController?){

        // TEST: Set interface from mock
        if(mockController != null){
            this.uiController = mockController
        }
        else{ // PRODUCTION: if no mock, get from context
            activity?.let {
                if(it is MainActivity){
                    try{
                        uiController = context as UIController
                    }catch (e: ClassCastException){
                        e.printStackTrace()
                    }
                }
            }
        }
    }

}