package com.tadese.framework.datasource.cache.model

import com.tadese.business.domain.model.post.Post
import com.tadese.business.domain.model.todo.Todo
import com.tadese.business.domain.util.EntityMapper
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PostCacheMapper
    @Inject
    constructor(): EntityMapper<PostEntity, Post> {

    override fun mapFromEntity(entity: PostEntity): Post {
        return Post(
            id =  entity.id,
            userId = entity.userId,
            title = entity.title,
            body = entity.body,
            comments = PostEntity.convertListOfCommentsToJson(entity.comments)
        )
    }

    override fun mapToEntity(domainModel: Post): PostEntity {
        return PostEntity(
            id = domainModel.id!!,
            userId = domainModel.userId!!,
            title = domainModel.title,
            body = domainModel.body,
            comments = PostEntity.convertListOfCommentsToString(domainModel.comments)
        )
    }

    fun mapFromEntityList(entities: List<PostEntity>): List<Post>{
        val data: ArrayList<Post> = ArrayList()
        for(entity in entities){
            data.add(mapFromEntity(entity))
        }

        return data
    }

    fun mapToEntityList(entities: List<Post>): List<PostEntity>{
        val data: ArrayList<PostEntity> = ArrayList()
        for(entity in entities){
            data.add(mapToEntity(entity))
        }

        return data
    }
}