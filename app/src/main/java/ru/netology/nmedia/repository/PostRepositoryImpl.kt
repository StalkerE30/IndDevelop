package ru.netology.nmedia.repository

import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import ru.netology.nmedia.api.*
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.entity.PostEntity
import ru.netology.nmedia.entity.toEntity
import ru.netology.nmedia.error.ApiError
import ru.netology.nmedia.error.AppError
import ru.netology.nmedia.error.NetworkError
import ru.netology.nmedia.error.UnknownError
import java.io.IOException


class PostRepositoryImpl(private val postDao: PostDao): PostRepository {

    override val data = postDao.getAll().map { it.map(PostEntity::toDto) }
        .flowOn(Dispatchers.Default)

    override fun getNewerCount(newerPostId: Long): Flow<Int> = flow {
        while (true) {
            try {
                delay(10_000)
                val response = PostsApi.retrofitService.getNewer(newerPostId)
                val body = response.body() ?: continue
                postDao.insert(body.toEntity(false))
                emit(body.size)
            } catch (e: Exception) {
                //ignore
            }
        }
    }

    override suspend fun update() {
        postDao.update()
    }

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
                val post = response.body() ?: throw ApiError(response.code(), response.message())
                postDao.insert(PostEntity.fromDto(post))
            } else {
                val response =PostsApi.retrofitService.unlikeById(id)
                if (!response.isSuccessful) {
                    throw RuntimeException(response.message())
                }
                val post = response.body() ?: throw ApiError(response.code(), response.message())
                postDao.insert(PostEntity.fromDto(post))
            }
            //postDao.likeById(id)
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
