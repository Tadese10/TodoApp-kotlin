package com.tadese.framework.presentation.todo

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.tadese.R
import com.tadese.business.domain.model.todo.Todo
import com.tadese.business.domain.state.DialogInputCaptureCallback
import com.tadese.business.domain.state.OnReloadCaptureCallback
import com.tadese.business.domain.state.StateMessageCallback
import com.tadese.business.interactors.authentication.UserLogin
import com.tadese.business.interactors.todo.GetAllTodoListInCache
import com.tadese.business.interactors.todo.GetAllTodoListInCache.Companion.GET_ALL_TODO_LIST_IN_CACHE_SUCCESS
import com.tadese.business.interactors.todo.GetAllTodoListInCache.Companion.GET_TODO_LIST_IN_CACHE_SUCCESS_WITH_EMPTY_LIST
import com.tadese.business.interactors.todo.GetAllTodoNumInCache.Companion.GET_TODO_TOTAL_NUM_IN_CACHE_EMPTY
import com.tadese.business.interactors.todo.GetAllTodoNumInCache.Companion.GET_TODO_TOTAL_NUM_IN_CACHE_SUCCESS
import com.tadese.business.interactors.todo.GetAllTodoOnNetworkByUserId
import com.tadese.business.interactors.todo.GetAllTodoOnNetworkByUserId.Companion.FETCHING_USER_TODOS_LIST_ON_NETWORK_EMPTY
import com.tadese.business.interactors.todo.GetAllTodoOnNetworkByUserId.Companion.FETCHING_USER_TODOS_LIST_ON_NETWORK_WAS_SUCCESSFUL
import com.tadese.business.interactors.todo.SearchTodoListInCache.Companion.SEARCH_TODO_NO_MATCHING_RESULTS
import com.tadese.business.interactors.todo.SearchTodoListInCache.Companion.SEARCH_TODO_SUCCESS
import com.tadese.framework.presentation.MainActivity
import com.tadese.framework.presentation.common.BaseFragment
import com.tadese.framework.presentation.common.TopSpacingItemDecoration
import com.tadese.framework.presentation.common.hideKeyboard
import com.tadese.framework.presentation.todo.state.TodoListInteractionManager
import com.tadese.framework.presentation.todo.state.TodoListToolbarState
import com.tadese.framework.presentation.todo.state.TodoStateEvent
import com.tadese.framework.presentation.todo.state.TodoViewState
import com.tadese.util.printLogD
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.fragment_todo.*

class TodoFragment
constructor(
    private val viewModelFactory: ViewModelProvider.Factory
): BaseFragment(R.layout.fragment_todo, false), ItemTouchHelperAdapter , TodoListAdapter.Interaction{

    private var listAdapter: TodoListAdapter? = null

    val viewModel: TodoViewModel by viewModels {
        viewModelFactory
    }

    private var itemTouchHelper: ItemTouchHelper? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI()
        setupRecyclerView()
        setupSwipeRefresh()
        setupFAB()
        subscribeObservers()

        restoreInstanceState(savedInstanceState)
    }

    override fun onPause() {
        super.onPause()
        saveLayoutManagerState()
    }

    override fun setTitle() = "Todo List"

    override fun inject() {
        getAppComponent().inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.setupChannel()
    }

    private fun restoreInstanceState(savedInstanceState: Bundle?) {
        savedInstanceState?.let { inState ->
            (inState[TODO_LIST_STATE_BUNDLE_KEY] as TodoViewState?)?.let { viewState ->
                viewModel.setViewState(viewState)
            }
        }
    }

    private fun saveLayoutManagerState(){
        recycler_view.layoutManager?.onSaveInstanceState()?.let { lmState ->
            viewModel.setLayoutManagerState(lmState)
        }
    }

    // Why didn't I use the "SavedStateHandle" here?
    // It sucks and doesn't work for testing
    override fun onSaveInstanceState(outState: Bundle) {
        val viewState = viewModel.viewState.value

        //clear the list. Don't want to save a large list to bundle.
        viewState?.userTodoList =  ArrayList()
        viewState?.latestUserTodoList =  ArrayList()

        outState.putParcelable(
            TODO_LIST_STATE_BUNDLE_KEY,
            viewState
        )
        super.onSaveInstanceState(outState)
    }

    private fun enableMultiSelectToolbarState(){
        view?.let { v ->
            val view = View.inflate(
                v.context,
                R.layout.layout_multiselection_toolbar,
                null
            )
            view.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
            var searchView =activity?.toolbar?.findViewById<LinearLayout>(R.id.search_content_container)
            searchView?.removeAllViews()
            searchView?.addView(view)
            setupMultiSelectionToolbar(view)
        }
    }

    private fun setupMultiSelectionToolbar(parentView: View){
        parentView
            .findViewById<ImageView>(R.id.action_exit_multiselect_state)
            .setOnClickListener {
                viewModel.setToolbarState(TodoListToolbarState.SearchViewState())
            }

        parentView
            .findViewById<ImageView>(R.id.action_delete_notes)
            .setOnClickListener {
               // deleteNotes()
                val mView = it
            }
    }


    private fun disableSearchViewToolbarState(){
        view?.let {
            var searchView =activity?.toolbar?.findViewById<LinearLayout>(R.id.search_content_container)
            searchView?.removeAllViews()
//            val view = toolbar_content_container
//                .findViewById<Toolbar>(R.id.toolbar)
//            toolbar_content_container.removeView(view)
        }
    }

    private fun enableSearchViewToolbarState(){
        view?.let { v ->
            val view = View.inflate(
                v.context,
                R.layout.layout_searchview_toolbar,
                null
            )
            view.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
            var searchView =activity?.toolbar?.findViewById<LinearLayout>(R.id.search_content_container)
            searchView?.removeAllViews()
            searchView?.addView(view)
            setupSearchView()
            setupFilterButton()
        }
    }

    override fun onResume() {
        super.onResume()
        OpenNewPage()
    }

//    fun reloadData(){
//        viewModel.clearList()
//        viewModel.getTodosInCache()
//        viewModel.retrieveNumTodosInCache()
//        viewModel.updateTodoListOnline()
//    }

    private fun setupFilterButton(){
        val searchViewToolbar: Toolbar? = activity?.toolbar
        searchViewToolbar?.findViewById<ImageView>(R.id.action_filter)?.setOnClickListener {
            //showFilterDialog()
        }
    }


    private fun setupSearchView(){

        val searchViewToolbar: Toolbar? = activity?.toolbar

        searchViewToolbar?.let { toolbar ->

            val searchView = toolbar.findViewById<SearchView>(R.id.search_view)

            val searchPlate: SearchView.SearchAutoComplete?
                    = searchView.findViewById(androidx.appcompat.R.id.search_src_text)
            searchPlate?.setHintTextColor(resources.getColor(R.color.search_hint_color))
            searchPlate?.setTextColor(resources.getColor(R.color.search_hint_color))

            searchPlate?.setOnEditorActionListener { v, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_UNSPECIFIED
                    || actionId == EditorInfo.IME_ACTION_SEARCH ) {
                    val searchQuery = v.text.toString()
                    viewModel.setQuery(searchQuery)
                    startNewSearch()
                    uiController.hideSoftKeyboard()
                }
                true
            }
        }
    }

    private fun startNewSearch() {
        viewModel.clearList()
        viewModel.searchPage()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        listAdapter = null // can leak memory
        itemTouchHelper = null // can leak memory
    }

    private fun disableMultiSelectToolbarState(){
        view?.let {
            var layout = activity?.toolbar?.findViewById<LinearLayout>(R.id.search_content_container)
            val view = layout?.findViewById<ConstraintLayout>(R.id.multiselect_toolbar)
            layout?.removeView(view)
            //viewModel.clearSelectedNotes()
        }
    }

    private fun subscribeObservers() {
        viewModel.toolbarState.observe(viewLifecycleOwner, Observer{ toolbarState ->

            when(toolbarState){

                is TodoListToolbarState.MultiSelectionState -> {
                    enableMultiSelectToolbarState()
                    disableSearchViewToolbarState()
                }

                is TodoListToolbarState.SearchViewState -> {
                    enableSearchViewToolbarState()
                    disableMultiSelectToolbarState()
                }
            }
        })

        viewModel.viewState.observe(viewLifecycleOwner, Observer{ viewState ->

            if(viewState != null){
                viewState.userTodoList?.let { noteList ->

                    if(!noteList.isNullOrEmpty()){
                         if(viewModel.isPaginationExhausted()
                            && !viewModel.isQueryExhausted()){
                            printLogD("Pagination", "Pagination is exhausted.")
                            viewModel.setQueryExhausted(true)
                            viewModel.setStateEvent(TodoStateEvent.GetAllUserTodoEvent(viewModel.getUserLoggedIn()?.id.toString()))// Fetch if there are new updates online
                        }
                    }
                        listAdapter?.submitList(noteList)
                        listAdapter?.notifyDataSetChanged()

                }

//                viewState.latestUserTodoList?.let { noteList ->
//                    var isupdate = !isUpdates()
//                    if(isupdate){
//                        viewModel.setTodoListData(noteList)
////                        listAdapter?.submitList(noteList)
////                        listAdapter?.notifyDataSetChanged()
//                    }
//                }

                // a note been inserted or selected
                viewState.newTodo?.let { newNote ->
                    //navigateToDetailFragment(newNote)
                }

                viewState.numTodosInCache?.let{
                    var total = it
                }

            }
        })

        viewModel.shouldDisplayProgressBar.observe(viewLifecycleOwner, Observer {
            //printActiveJobs()
            uiController.displayProgressBar(it)
        })

        viewModel.stateMessage.observe(viewLifecycleOwner, Observer { stateMessage ->

            stateMessage?.response?.let {
                it.message?.let {
                    when (it) {

                        GET_ALL_TODO_LIST_IN_CACHE_SUCCESS -> {
                            viewModel.clearStateMessage()
                        }

                        GET_TODO_LIST_IN_CACHE_SUCCESS_WITH_EMPTY_LIST -> {
                            viewModel.clearStateMessage()
                        }

                        GET_TODO_TOTAL_NUM_IN_CACHE_SUCCESS -> {
                            viewModel.clearStateMessage()
                        }

                        GET_TODO_TOTAL_NUM_IN_CACHE_EMPTY -> {
                            viewModel.clearStateMessage()
                        }

                        SEARCH_TODO_SUCCESS -> {
                            viewModel.clearStateMessage()
                        }

                        SEARCH_TODO_NO_MATCHING_RESULTS ->{
                            uiController.onResponseReceived(
                                response = stateMessage.response,
                                stateMessageCallback = object : StateMessageCallback {
                                    override fun removeMessageFromStack() {
                                        viewModel.clearStateMessage()
                                    }
                                }
                            )
                        }

                        FETCHING_USER_TODOS_LIST_ON_NETWORK_WAS_SUCCESSFUL -> {
                            viewModel.clearStateMessage()

                                 uiController.displayLatestChangesNotification(
                                true,
                                "New updates received.",
                                object : OnReloadCaptureCallback {
                                    override fun onReloadCaptured() {
                                        viewModel.getCurrentViewStateOrNew().latestUserTodoList?.let {
                                            OpenNewPage()
                                        }
                                    }

                                })

                        }

                        FETCHING_USER_TODOS_LIST_ON_NETWORK_EMPTY -> {
                            uiController.onResponseReceived(
                                response = stateMessage.response,
                                stateMessageCallback = object : StateMessageCallback {
                                    override fun removeMessageFromStack() {
                                        viewModel.clearStateMessage()
                                    }
                                }
                            )
                        }

                        else ->{
                            viewModel.clearStateMessage()
                        }

                    }
                }

            }
        })

    }

    private fun isUpdates() = viewModel.getCurrentViewStateOrNew().userTodoList.isNullOrEmpty()

    private fun setupFAB() {
        add_new_note_fab.setOnClickListener {
            uiController.displayInputCaptureDialog(
                getString(R.string.text_enter_a_title),
                object: DialogInputCaptureCallback {
                    override fun onTextCaptured(text: String) {
//                        val newNote = viewModel.createNewNote(title = text)
//                        viewModel.setStateEvent(
//                            InsertNewNoteEvent(
//                                title = newNote.title
//                            )
//                        )
                    }
                }
            )
        }
    }

    private fun setupSwipeRefresh() {
        swipe_refresh.setOnRefreshListener {
            OpenNewPage()
            swipe_refresh.isRefreshing = false
        }
    }

    private fun OpenNewPage() {
        printLogD("DCM", "start new search")
        viewModel.clearList()
        viewModel.loadFirstPage()
    }


    private fun setupRecyclerView() {
        recycler_view.apply {
            layoutManager = LinearLayoutManager(activity)
            addItemDecoration(TopSpacingItemDecoration(20))
            itemTouchHelper = ItemTouchHelper(
                NoteItemTouchHelperCallback(
                    this@TodoFragment,
                    viewModel.todoListInteractionManager
                )
            )
            listAdapter = TodoListAdapter(
                this@TodoFragment,
                viewLifecycleOwner,
                viewModel.todoListInteractionManager.selectedTodos,
            )
            itemTouchHelper?.attachToRecyclerView(this)
            addOnScrollListener(object: RecyclerView.OnScrollListener(){
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val lastPosition = layoutManager.findLastVisibleItemPosition()
                    if (lastPosition == listAdapter?.itemCount?.minus(1)) {
                            viewModel.nextPage()
                    }
                }
            })
            adapter = listAdapter
        }
    }


    private fun setupUI() {
        view?.hideKeyboard()
    }

    override fun onItemSwiped(position: Int) {
//        if(!viewModel.isDeletePending()){
//            listAdapter?.getNote(position)?.let { note ->
//                viewModel.beginPendingDelete(note)
//            }
//        }
//        else{
//            listAdapter?.notifyDataSetChanged()
//        }
    }

    override fun onItemSelected(position: Int, item: Todo) {
        if(isMultiSelectionModeEnabled()){
            viewModel.addOrRemoveNoteFromSelectedList(item)
        }
        else{
            viewModel.setTodo(item)
        }
    }

    override fun restoreListPosition() {
        viewModel.getLayoutManagerState()?.let { lmState ->
            recycler_view?.layoutManager?.onRestoreInstanceState(lmState)
        }
    }

    override fun isMultiSelectionModeEnabled()
            = viewModel.isMultiSelectionStateActive()

    override fun activateMultiSelectionMode()
            = viewModel.setToolbarState(TodoListToolbarState.MultiSelectionState())

    override fun isTodoSelected(note: Todo): Boolean {
        return viewModel.isTodoSelected(note)
    }

    companion object {
        const val FETCH_TODO_SUCCESS = "Fetched Todo List"
        const val EMPTY_TODO_LIST = "Empty List found"
        const val FETCHING_USER_TODOS_LIST_WAS_SUCCESSFUL = "FETCHING_USER_TODOS_LIST_WAS_SUCCESSFUL"
        const val FETCHING_USER_TODOS_LIST_EMPTY = "FETCHING_USER_TODOS_LIST_EMPTY"
        const val TODO_LIST_STATE_BUNDLE_KEY = "com.tadese.framework.presentation.todo.framework.presentation.todo.state"
    }
}