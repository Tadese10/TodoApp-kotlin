package com.tadese.framework.datasource.cache.model

import com.tadese.business.domain.model.post.Post
import com.tadese.business.domain.model.todo.Todo
import com.tadese.business.domain.util.EntityMapper
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TodoCacheMapper
    @Inject
    constructor(): EntityMapper<TodoEntity, Todo> {

    override fun mapFromEntity(entity: TodoEntity): Todo {
        return Todo(
            id = entity.id,
            userId = entity.userId,
            title =  entity.title,
            completed = entity.completed
        )
    }

    fun mapFromEntityList(entities: List<TodoEntity>): List<Todo>{
        val data: ArrayList<Todo> = ArrayList()
        for(entity in entities){
            data.add(mapFromEntity(entity))
        }

        return data
    }

    fun mapToEntityList(entities: List<Todo>): List<TodoEntity>{
        val data: ArrayList<TodoEntity> = ArrayList()
        for(entity in entities){
            data.add(mapToEntity(entity))
        }

        return data
    }

    override fun mapToEntity(domainModel: Todo): TodoEntity {
        return TodoEntity(
            id = domainModel.id,
            userId = domainModel.userId,
            title = domainModel.title,
            completed = domainModel.completed
        )
    }

}