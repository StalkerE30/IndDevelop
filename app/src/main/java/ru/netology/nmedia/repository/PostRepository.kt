package ru.netology.nmedia.repository

import kotlinx.coroutines.flow.Flow
import ru.netology.nmedia.dto.Post
//import ru.netology.nmedia.dto.Media
import java.io.File
//import ru.netology.nmedia.dto.MediaUpload


interface PostRepository {
    val data: Flow<List<Post>>
    fun getNewerCount(newerPostId:Long):Flow<Int>
    suspend fun getAll()
    suspend fun update()
    suspend fun likeById(id: Long,likeUnLike:Boolean)
    suspend fun save(post:Post)
    suspend fun removeById(id: Long)
    //suspend fun saveWithAttachment(post: Post, upload: MediaUpload)
    suspend fun saveWithAttachment(post: Post, file: File)
    //suspend fun upload(upload: MediaUpload): Media

}
