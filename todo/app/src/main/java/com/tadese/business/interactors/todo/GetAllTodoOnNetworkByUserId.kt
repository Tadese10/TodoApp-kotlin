package com.tadese.business.interactors.todo

import com.example.cleanarchitecture.business.data.util.appApiCall
import com.example.cleanarchitecture.business.data.util.appCacheCall
import com.tadese.business.data.cache.abstraction.AppCacheDataSource
import com.tadese.business.data.network.ApiResponseHandler
import com.tadese.business.data.network.abstraction.AppNetworkDatasource
import com.tadese.business.domain.model.todo.Todo
import com.tadese.business.domain.state.DataState
import com.tadese.business.domain.state.MessageType
import com.tadese.business.domain.state.Response
import com.tadese.business.domain.state.UIComponentType
import com.tadese.framework.presentation.todo.state.TodoStateEvent
import com.tadese.framework.presentation.todo.state.TodoViewState
import com.tadese.util.printLogD
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetAllTodoOnNetworkByUserId(
    private val todoNetworkDataSource: AppNetworkDatasource,
    private val appCacheDataSource: AppCacheDataSource
)
{
    fun getAllTodoByUserId(
        stateEvent : TodoStateEvent.GetAllUserTodoEvent
    ) : Flow<DataState<TodoViewState>> = flow{

        var cachedTodo = appCacheDataSource.getAllTodo()
        printLogD("Cached Todo Data ", cachedTodo.toString())

        val networkResult = appApiCall(Dispatchers.IO){
            todoNetworkDataSource.getAllTodoByUserId(stateEvent.username)
        }

        var handler = object : ApiResponseHandler<TodoViewState, List<Todo>>(
            response = networkResult,
            stateEvent = stateEvent
        ) {

            override suspend fun handleSuccess(resultObj: List<Todo>): DataState<TodoViewState> {
                printLogD("NetworkResponse", resultObj.toString())

                val viewState = TodoViewState(
                    latestUserTodoList = resultObj,
                )

                if(resultObj.isNullOrEmpty()){
                  return  DataState.data(
                        response = Response(
                            message = FETCHING_USER_TODOS_LIST_ON_NETWORK_EMPTY,
                            uiComponentType = UIComponentType.Toast(),
                            messageType = MessageType.Success()
                        ),
                        data = viewState,
                        stateEvent = stateEvent
                    )
                }
                else{
                    if(resultObj != cachedTodo){
                        return  DataState.data(
                            response = Response(
                                message = FETCHING_USER_TODOS_LIST_ON_NETWORK_WAS_SUCCESSFUL,
                                uiComponentType = UIComponentType.None(),
                                messageType = MessageType.Success()
                            ),
                            data = viewState,
                            stateEvent = stateEvent
                        )
                    }else{
                       return DataState.data(
                            response = Response(
                                message = FETCHING_USER_TODOS_LIST_ON_NETWORK_EMPTY,
                                uiComponentType = UIComponentType.Toast(),
                                messageType = MessageType.Success()
                            ),
                            data = viewState,
                            stateEvent = stateEvent
                        )
                    }

                }

            }

        }.getResult()

            printLogD(handler.javaClass.name, handler.toString())

            handler.data?.latestUserTodoList?.let {
                saveUsersTodoToCachedata(it!!, cachedTodo)//Cache the response
            }

        emit(handler)

    }

    private suspend fun saveUsersTodoToCachedata(userTodoList: List<Todo>, cachedTodo: List<Todo>) {
        //Check if network returned same cached data

        if(cachedTodo.isNullOrEmpty()){
            save(userTodoList)
            printLogD("Todo Cached Data ", "Todo Cached Data is null")
        }
        else{
            if(userTodoList != cachedTodo){
                printLogD("Differences in Net and Cached Data", "Yes")
                var dataToBeDeletedIds = userTodoList.filter { todo -> cachedTodo.any { it.id == todo!!.id } }.map { it.id }
                if(!dataToBeDeletedIds.isNullOrEmpty()){
                    appCacheDataSource.deleteTodos(dataToBeDeletedIds)
                    printLogD("Deleted todos: ", dataToBeDeletedIds.toString())
                }
                save(userTodoList)
            }else
                printLogD("Differences in Net and Cached Data", "No")

        }
    }

    suspend fun save(data: List<Todo>){
        appCacheCall(Dispatchers.IO){
            appCacheDataSource.saveUserTodos(data)
        }
    }

    companion object{
        const val FETCHING_USER_TODOS_LIST_ON_NETWORK_WAS_SUCCESSFUL = "Successfully fetched Todo online."
        const val FETCHING_USER_TODOS_LIST_ON_NETWORK_EMPTY = "Todo list is empty."
    }
}