package com.tadese.framework.datasource.cache.database

import androidx.room.*
import com.tadese.framework.datasource.cache.model.PostEntity
import com.tadese.framework.datasource.cache.model.TodoEntity
import com.tadese.framework.datasource.cache.model.UsersEntity

const val TODO_PAGINATION_PAGE_SIZE = 30

@Dao
interface TodoDao {

    @Insert
    suspend fun insertPost(post : PostEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPosts(notes: List<PostEntity>): LongArray

    @Query("DELETE FROM ${PostEntity.name} WHERE id IN (:ids)")
    suspend fun deletePosts(ids: List<String>): Int

    @Query("DELETE FROM ${PostEntity.name}")
    suspend fun deleteAllPosts(): Int

    @Query("SELECT * FROM ${PostEntity.name}")
    suspend fun getAllPosts(): List<PostEntity>

    @Query(
        """
            UPDATE ${PostEntity.name}
            SET 
            title = :title,
            body = :body,
            comments = :comments
            WHERE id = :id
        """
    )
    suspend fun updatePost(id : Int,
                           title: String,
                           body : String,
                           comments: String): Int

    @Query("SELECT COUNT(*) FROM ${PostEntity.name}")
    suspend fun getNumPosts(): Int

    @Query("""
        SELECT * FROM ${PostEntity.name} 
        WHERE title LIKE '%' || :query || '%' 
        OR userId LIKE '%' || :query || '%' 
        OR id LIKE '%' || :query || '%' 
        LIMIT (:page * :pageSize)
        """)
    suspend fun searchPosts(query: String,page: Int, pageSize: Int =  TODO_PAGINATION_PAGE_SIZE): List<PostEntity>


    @Query("""
        SELECT * FROM ${PostEntity.name} 
        WHERE id = :id
        """)
    suspend fun findPostById(id: Int): PostEntity



    @Insert
    suspend fun insertTodo(post : TodoEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTodos(todos: List<TodoEntity>): LongArray

    @Query("DELETE FROM ${TodoEntity.name} WHERE id IN (:ids)")
    suspend fun deleteTodos(ids: List<String>): Int

    @Query("DELETE FROM ${TodoEntity.name}")
    suspend fun deleteAllTodos(): Int

    @Query("SELECT * FROM ${TodoEntity.name}")
    suspend fun getAllTodos(): List<TodoEntity>

    @Query("""
        SELECT * FROM ${TodoEntity.name}
        WHERE id = :id
        """)
    suspend fun getTodoById(id: Int): TodoEntity

    @Query(
        """
            UPDATE ${TodoEntity.name}
            SET 
            title = :title
            WHERE id = :id
        """
    )
    suspend fun updateTodo(id : Int,
                           title: String): Int

    @Query("SELECT COUNT(*) FROM ${TodoEntity.name}")
    suspend fun getNumTodos(): Int

    @Query("""
        SELECT * FROM ${TodoEntity.name} 
        WHERE title LIKE '%' || :query || '%' 
        OR userId LIKE '%' || :query || '%' 
        OR id LIKE '%' || :query || '%' 
        LIMIT (:page * :pageSize)
        """)
    suspend fun searchTodos(query: String, page: Int , pageSize : Int = TODO_PAGINATION_PAGE_SIZE): List<TodoEntity>

    @Query("""
        SELECT * FROM ${TodoEntity.name} 
        WHERE id = :id
        """)
    suspend fun findTodoById(id: Int): TodoEntity


    @Insert
    suspend fun insertLoggedInUser(user : UsersEntity): Long

    @Query("SELECT * FROM ${UsersEntity.table_name}")
    suspend fun findLoggedInUser(): List<UsersEntity>


}