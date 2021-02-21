package com.tadese.business.data.network.implementation

import com.tadese.business.data.network.abstraction.TodoNetworkDatasource
import com.tadese.business.domain.model.comment.Comment
import com.tadese.business.domain.model.login.LoginUser
import com.tadese.business.domain.model.post.Post
import com.tadese.business.domain.model.todo.Todo

class Test : TodoNetworkDatasource {
    override suspend fun loginUser(username: String): LoginUser? {
        TODO("Not yet implemented")
    }

    override suspend fun addTodo(todo: Todo): Todo? {
        TODO("Not yet implemented")
    }

    override suspend fun getAllTodoByUserId(userId: String): List<Todo> {
        TODO("Not yet implemented")
    }

    override suspend fun getAllTodo(): List<Todo> {
        TODO("Not yet implemented")
    }

    override suspend fun addPostComment(comment: Comment): Comment {
        TODO("Not yet implemented")
    }

    override suspend fun addPost(post: Post): Post {
        TODO("Not yet implemented")
    }

    override suspend fun getAllPost(): List<Post> {
        TODO("Not yet implemented")
    }

    override suspend fun findPostById(potsId: Int): Post? {
        TODO("Not yet implemented")
    }

    override suspend fun findCommentById(commentId: Int): Comment? {
        TODO("Not yet implemented")
    }

    override suspend fun getPostByUserId(userId: Int): List<Post> {
        TODO("Not yet implemented")
    }

    override suspend fun getPostCommentsByPostId(postId: Int): List<Comment> {
        TODO("Not yet implemented")
    }
}