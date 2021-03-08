package com.tadese.framework.presentation.todo.state

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.tadese.business.domain.model.todo.Todo

class TodoListInteractionManager {

    private val _selectedTodos: MutableLiveData<ArrayList<Todo>> = MutableLiveData()

    private val _toolbarState: MutableLiveData<TodoListToolbarState>
            = MutableLiveData(TodoListToolbarState.SearchViewState())

    val selectedTodos: LiveData<ArrayList<Todo>>
        get() = _selectedTodos

    val toolbarState: LiveData<TodoListToolbarState>
        get() = _toolbarState

    fun setToolbarState(state: TodoListToolbarState){
        _toolbarState.value = state
    }

    fun getSelectedTodos():ArrayList<Todo> = _selectedTodos.value?: ArrayList()

    fun isMultiSelectionStateActive(): Boolean{
        return _toolbarState.value.toString() == TodoListToolbarState.MultiSelectionState().toString()
    }

    fun addOrRemoveNoteFromSelectedList(note: Todo){
        var list = _selectedTodos.value
        if(list == null){
            list = ArrayList()
        }
        if (list.contains(note)){
            list.remove(note)
        }
        else{
            list.add(note)
        }
        _selectedTodos.value = list
    }

    fun isTodoSelected(note: Todo): Boolean{
        return _selectedTodos.value?.contains(note)?: false
    }

    fun clearSelectedNotes(){
        _selectedTodos.value = null
    }


}