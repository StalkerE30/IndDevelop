package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import okio.IOException
import ru.netology.nmedia.api.PostsApi
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.entity.PostEntity
import ru.netology.nmedia.error.ApiError
import ru.netology.nmedia.error.NetworkError
import ru.netology.nmedia.error.UnknownError
import kotlin.RuntimeException


class PostRepositoryImpl(private val postDao: PostDao): PostRepository {


    //    override fun likeById(id: Long, likeUnLike:Boolean,callback: PostRepository.Callback<Post>) {
//        if (likeUnLike) {
//            PostsApi.retrofitService.likeById(id)
//                .enqueue(object : Callback<Post>{
//                    override fun onResponse(call: Call<Post>, response: Response<Post>) {
//                        if (!response.isSuccessful) {
//                            callback.onError(RuntimeException(response.message()))
//                            return
//                        } else
//                            callback.onSuccess(response.body() ?: throw RuntimeException("body is null"))
//                    }
//
//                    override fun onFailure(call: Call<Post>, t: Throwable) {
//                        callback.onError(RuntimeException(t))
//                    }
//                })
//        } else {
//            PostsApi.retrofitService.unlikeById(id)
//                .enqueue(object : Callback<Post>{
//                    override fun onResponse(call: Call<Post>, response: Response<Post>) {
//                        if (!response.isSuccessful) {
//                            callback.onError(RuntimeException(response.message()))
//                            return
//                        } else
//                            callback.onSuccess(response.body() ?: throw RuntimeException("body is null"))
//                    }
//                    override fun onFailure(call: Call<Post>, t: Throwable) {
//                        callback.onError(RuntimeException(t))
//                    }
//                })
//        }
//    }
    override val data: LiveData<List<Post>> = postDao.getAll().map { it.map(PostEntity::toDto) }

    override suspend fun getAll() {
        try {
            val response = PostsApi.retrofitService.getAll()
            if (!response.isSuccessful) {
                throw RuntimeException(response.message())
            }
            val posts = response.body() ?: throw ApiError(response.code(), response.message())
            postDao.insert(posts.map(PostEntity::fromDto))
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }

    }

    override suspend fun likeById(id: Long, likeUnLike: Boolean) {
        try {
            if (likeUnLike) {
                val response = PostsApi.retrofitService.likeById(id)
                if (!response.isSuccessful) {
                    throw RuntimeException(response.message())
                }
            } else {
                val response =PostsApi.retrofitService.unlikeById(id)
                if (!response.isSuccessful) {
                    throw RuntimeException(response.message())
                }
            }
            postDao.likeById(id)
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun save(post: Post) {
        try {
            val response = PostsApi.retrofitService.save(post)
            if (!response.isSuccessful) {
                throw RuntimeException(response.message())
            }
            val post = response.body() ?: throw ApiError(response.code(), response.message())
            postDao.save(PostEntity.fromDto(post))
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun removeById(id: Long) {
        try {
            val response = PostsApi.retrofitService.removeById(id)
            if (!response.isSuccessful) {
                throw RuntimeException(response.message())
            }
            postDao.removeById(id)
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }



}
