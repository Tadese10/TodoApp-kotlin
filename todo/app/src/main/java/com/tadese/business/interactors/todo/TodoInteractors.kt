package com.tadese.business.interactors.todo

class TodoInteractors (
    val addTodoToNetworkAndSaveInCache: AddTodoToNetworkAndSaveInCache,
    val getAllTodoOnNetworkByUserId: GetAllTodoOnNetworkByUserId,
    val searchTodoListInCache: SearchTodoListInCache,
    val getAllTodoListInCache: GetAllTodoListInCache,
    val getAllTodoNumInCache: GetAllTodoNumInCache,
    val searchTodoListInCacheById: SearchTodoListInCacheById,
    val getAllTodoNumInCacheWithQuery: GetAllTodoNumInCacheWithQuery,
    val deleteAllTodoUserInCache: DeleteAllTodoUserInCache
)