package com.tadese.framework.presentation.todo

import android.content.SharedPreferences
import android.os.Parcelable
import androidx.lifecycle.LiveData
import com.tadese.business.domain.model.login.LoginUser
import com.tadese.business.domain.model.todo.Todo
import com.tadese.business.domain.state.DataState
import com.tadese.business.domain.state.StateEvent
import com.tadese.business.interactors.common.CommonInteractors
import com.tadese.business.interactors.todo.TodoInteractors
import com.tadese.framework.datasource.cache.database.TODO_FILTER_TITLE
import com.tadese.framework.datasource.preferences.PreferenceKeys.Companion.TODO_FILTER
import com.tadese.framework.presentation.common.BaseViewModel
import com.tadese.framework.presentation.todo.state.TodoListInteractionManager
import com.tadese.framework.presentation.todo.state.TodoListToolbarState
import com.tadese.framework.presentation.todo.state.TodoStateEvent
import com.tadese.framework.presentation.todo.state.TodoViewState
import com.tadese.util.printLogD
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.runBlocking

@ExperimentalCoroutinesApi
@FlowPreview
class TodoViewModel
    (
    private val todoInteractors: TodoInteractors,
    private val commonInteractors: CommonInteractors,
    private val editor: SharedPreferences.Editor,
    private val sharedPreferences: SharedPreferences
): BaseViewModel<TodoViewState>() {

    val todoListInteractionManager =
        TodoListInteractionManager()

    val toolbarState: LiveData<TodoListToolbarState>
        get() = todoListInteractionManager.toolbarState //Get the toolbar state


    fun addOrRemoveNoteFromSelectedList(note: Todo)
            = todoListInteractionManager.addOrRemoveNoteFromSelectedList(note)


    init {
        setTodoFilter(
            sharedPreferences.getString(
                TODO_FILTER,
                TODO_FILTER_TITLE
            )
        )
    }

    fun clearList(){
        printLogD("ListViewModel", "clearList")
        val update = getCurrentViewStateOrNew()
        update.userTodoList = ArrayList()
        update.latestUserTodoList = ArrayList()
        setViewState(update)
    }

    fun retrieveNumTodosInCache(){
        setStateEvent(TodoStateEvent.GetAllUserTodoNumInCacheEvent())
    }

    fun getTodosInCache(){
        setQueryExhausted(false)
        setStateEvent(TodoStateEvent.GetAllUserTodoInCacheEvent(viewState.value?.page!!))
    }

    fun getUserLoggedIn(): LoginUser?{
        var userHasLoggedIn : LoginUser? = null
        runBlocking {
            userHasLoggedIn = commonInteractors.getSavedUserData.get()
        }
        return userHasLoggedIn
    }

    fun loadFirstPage() {
        setQueryExhausted(false)
        resetPage(true)
        //setStateEvent(TodoStateEvent.DeleteAllTodoUserInCacheEvent())
        setStateEvent(TodoStateEvent.GetAllUserTodoNumInCacheEvent())
        setStateEvent(TodoStateEvent.GetAllUserTodoInCacheEvent(viewState.value?.page!!))
        setStateEvent(TodoStateEvent.GetAllUserTodoEvent(getUserLoggedIn()?.id.toString()))
        printLogD("TodoListViewModel",
            "loadFirstPage: ${getCurrentViewStateOrNew().searchQuery}")
    }

    fun setQuery(query: String?){
        val update =  getCurrentViewStateOrNew()
        update.searchQuery = query
        setViewState(update)
    }

    fun resetPage(resetQuery : Boolean = false){
        val update = getCurrentViewStateOrNew()
        update.page = 1
        update.numTodosInCache = null
        update.isSearching = false
        if(resetQuery)
            update.searchQuery = null
        update.newTodo = null
        setViewState(update)
    }


    fun setQueryExhausted(isExhausted: Boolean){
        val update = getCurrentViewStateOrNew()
        update.isQueryExhausted = isExhausted
        setViewState(update)
    }


    fun getLayoutManagerState(): Parcelable? {
        return getCurrentViewStateOrNew().layoutManagerState
    }

    fun isMultiSelectionStateActive()
            = todoListInteractionManager.isMultiSelectionStateActive()

    fun setToolbarState(state: TodoListToolbarState)
            = todoListInteractionManager.setToolbarState(state)

    private fun setTodoFilter(string: String?) {

    }

    fun isTodoSelected(note: Todo): Boolean
            = todoListInteractionManager.isTodoSelected(note)

    override fun handleNewData(data: TodoViewState) {
        data?.let { viewState ->
            viewState.userTodoList?.let {
                setTodoListData(it)
            }

            viewState.numTodosInCache?.let {
                setTodosNumInCache(it)
            }

            viewState.latestUserTodoList?.let {
                setNewTodoListData(it)
            }

            viewState.newTodo?.let {
                setTodo(it)
            }
        }
    }

    private fun setTodosNumInCache(it: Int) {
        val update = getCurrentViewStateOrNew()
        update.numTodosInCache = it
        setViewState(update)
    }

    fun setTodo(it: Todo) {
        val update = getCurrentViewStateOrNew()
        update.newTodo = it
        setViewState(update)
    }

    private fun setNewTodoListData(it: List<Todo>) {
        val update = getCurrentViewStateOrNew()
        update.latestUserTodoList = it
        setViewState(update)
    }

     fun setTodoListData(it: List<Todo>) {
        val update = getCurrentViewStateOrNew()
        update.userTodoList = it
        setViewState(update)
    }

    override fun setStateEvent(stateEvent: StateEvent) {
        val job: Flow<DataState<TodoViewState>?> = when(stateEvent){
            is TodoStateEvent.AddTodoEvent ->{
                todoInteractors.addTodoToNetworkAndSaveInCache.addTodo(
                    stateEvent = stateEvent
                )
            }
            is TodoStateEvent.GetAllUserTodoEvent ->{
                todoInteractors.getAllTodoOnNetworkByUserId.getAllTodoByUserId(
                    stateEvent = stateEvent
                )
            }
            is TodoStateEvent.GetAllUserTodoInCacheEvent ->{
                todoInteractors.getAllTodoListInCache.getAll(
                    stateEvent = stateEvent
                )
            }
            is TodoStateEvent.GetAllUserTodoNumInCacheWithQueryEvent ->{
                todoInteractors.getAllTodoNumInCacheWithQuery.getNum(
                    stateEvent = stateEvent
                )
            }
            is TodoStateEvent.GetAllUserTodoNumInCacheEvent ->{
                todoInteractors.getAllTodoNumInCache.getNum(
                    stateEvent = stateEvent
                )
            }
            is TodoStateEvent.SearchTodoListEvent -> {
                todoInteractors.searchTodoListInCache.searchTodoList(
                    stateEvent = stateEvent
                )
            }
            is TodoStateEvent.SearchTodoByIdEvent ->{
                todoInteractors.searchTodoListInCacheById.searchTodoListById(
                    stateEvent = stateEvent
                )
            }
            is TodoStateEvent.DeleteAllTodoUserInCacheEvent ->{
                todoInteractors.deleteAllTodoUserInCache.delete(
                    stateEvent = stateEvent
                )
            }
            else -> {
                emitInvalidStateEvent(stateEvent)
            }
        }

        launchJob(stateEvent, job)
    }

    override fun initNewViewState(): TodoViewState {
        return TodoViewState()
    }

    private fun incrementPageNumber(){
        val update = getCurrentViewStateOrNew()
        val page = update.copy().page ?: 1
        update.page = page.plus(1)
        setViewState(update)
    }

    fun setLayoutManagerState(layoutManagerState: Parcelable){
        val update = getCurrentViewStateOrNew()
        update.layoutManagerState = layoutManagerState
        setViewState(update)
    }

    fun getTodoListSize() : Int {
        printLogD("getTodList: ", getCurrentViewStateOrNew().userTodoList?.size.toString()?: 0.toString())
        return getCurrentViewStateOrNew().userTodoList?.size?: 0
    }

    fun clearLayoutManagerState(){
        val update = getCurrentViewStateOrNew()
        update.layoutManagerState = null
        setViewState(update)
    }

    fun updateTodoListOnline() {
        setStateEvent(TodoStateEvent.GetAllUserTodoEvent(getUserLoggedIn()!!.id.toString()))
    }

    private fun getNumTodosInCache() : Int{
        printLogD("getNumTodosInCache",
            getCurrentViewStateOrNew().numTodosInCache.toString()?: 0.toString())
        return getCurrentViewStateOrNew().numTodosInCache?: 0
    }

    fun isPaginationExhausted() = getTodoListSize() >= getNumTodosInCache()


    fun isQueryExhausted(): Boolean{
        printLogD("NoteListViewModel",
            "is query exhausted? ${getCurrentViewStateOrNew().isQueryExhausted?: true}")
        return getCurrentViewStateOrNew().isQueryExhausted?: true
    }


    fun nextPage(){
        if(!isQueryExhausted()){
            printLogD("NoteListViewModel", "attempting to load next page...")
            clearLayoutManagerState()
            incrementPageNumber()
            if(getCurrentViewStateOrNew().isSearching)
                setStateEvent(TodoStateEvent.SearchTodoListEvent(query = getCurrentViewStateOrNew().searchQuery!!,"", getCurrentViewStateOrNew().page!!))
            else
                setStateEvent(TodoStateEvent.GetAllUserTodoInCacheEvent(getCurrentViewStateOrNew().page!!))
        }
    }

    fun searchPage() {
        setQueryExhausted(false)
        resetPage()
        setIsSearching(true)
            setStateEvent(TodoStateEvent.GetAllUserTodoNumInCacheWithQueryEvent(getCurrentViewStateOrNew().searchQuery!!))
            setStateEvent(TodoStateEvent.SearchTodoListEvent(query = getCurrentViewStateOrNew().searchQuery!!,"", getCurrentViewStateOrNew().page!!))
               printLogD("SearchTodoListViewModel",
            "loadFirstPage: ${getCurrentViewStateOrNew().searchQuery}")
    }

    fun setIsSearching(data: Boolean) {
        val update = getCurrentViewStateOrNew()
        update.isSearching = data
        setViewState(update)
    }

    fun loadUpdates() {
        setQueryExhausted(false)
        resetPage()
        setStateEvent(TodoStateEvent.GetAllUserTodoNumInCacheEvent())
        setStateEvent(TodoStateEvent.GetAllUserTodoInCacheEvent(viewState.value?.page!!))
        setStateEvent(TodoStateEvent.GetAllUserTodoEvent(getUserLoggedIn()?.id.toString()))
        printLogD("UpdateTodoListViewModel",
            "loadFirstPage: ${getCurrentViewStateOrNew().searchQuery}")
    }

}