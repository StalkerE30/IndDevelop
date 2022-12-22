package ru.netology.nmedia.repository

import kotlinx.coroutines.flow.Flow
import ru.netology.nmedia.dto.Post

interface PostRepository {
    val data: Flow<List<Post>>
    fun getNewerCount(newerPostId:Long):Flow<Int>
    suspend fun getAll()
    suspend fun update()
    suspend fun likeById(id: Long,likeUnLike:Boolean)
    suspend fun save(post:Post)
    suspend fun removeById(id: Long)

}
