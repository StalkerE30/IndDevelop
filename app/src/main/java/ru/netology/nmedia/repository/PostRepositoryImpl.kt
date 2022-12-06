package ru.netology.nmedia.repository

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.netology.nmedia.api.PostsApi
import ru.netology.nmedia.dto.Post
import java.lang.RuntimeException


class PostRepositoryImpl: PostRepository {

    override fun getAll(callback: PostRepository.Callback<List<Post>>) {
        PostsApi.retrofitService.getAll()
            .enqueue(object : Callback<List<Post>> {
                override fun onResponse(call: Call<List<Post>>, response: Response<List<Post>>) {
                    if (!response.isSuccessful) {
                        callback.onError(RuntimeException(response.message()))
                        return
                    } else if (response.code()<200 || response.code()<299 ) {
                        val resp = response.code()
                        callback.onError(RuntimeException("Сервер вернул ошибку $resp"))
                    } else
                    callback.onSuccess(response.body() ?: throw RuntimeException("body is null"))
                }

                override fun onFailure(call: Call<List<Post>>, t: Throwable) {
                    TODO("Not yet implemented")
                }
            })
    }

    override fun likeById(id: Long, likeUnLike:Boolean,callback: PostRepository.Callback<Post>) {
        if (likeUnLike) {
            PostsApi.retrofitService.likeById(id)
                .enqueue(object : Callback<Post>{
                    override fun onResponse(call: Call<Post>, response: Response<Post>) {
                        if (!response.isSuccessful) {
                            callback.onError(RuntimeException(response.message()))
                            return
                        } else if (response.code()<200 || response.code()<299 ) {
                            val resp = response.code()
                            callback.onError(RuntimeException("Сервер вернул ошибку $resp"))
                        } else

                        callback.onSuccess(response.body() ?: throw RuntimeException("body is null"))
                    }

                    override fun onFailure(call: Call<Post>, t: Throwable) {
                        TODO("Not yet implemented")
                    }
                })
        } else {
            PostsApi.retrofitService.unlikeById(id)
                .enqueue(object : Callback<Post>{
                    override fun onResponse(call: Call<Post>, response: Response<Post>) {
                        if (!response.isSuccessful) {
                            callback.onError(RuntimeException(response.message()))
                            return
                        }

                        callback.onSuccess(response.body() ?: throw RuntimeException("body is null"))
                    }
                    override fun onFailure(call: Call<Post>, t: Throwable) {
                        TODO("Not yet implemented")
                    }
                })
        }
    }

    override fun save(post:Post, callback: PostRepository.Callback<Post>) {
        PostsApi.retrofitService.save(post)
            .enqueue(object :Callback<Post>{
                override fun onResponse(call: Call<Post>, response: Response<Post>) {
                    if (!response.isSuccessful) {
                        callback.onError(RuntimeException(response.message()))
                        return
                    }
                    callback.onSuccess(response.body() ?: throw RuntimeException("body is null"))
                }

                override fun onFailure(call: Call<Post>, t: Throwable) {
                    TODO("Not yet implemented")
                }

            })
    }

    override fun removeById(id: Long,callback: PostRepository.Callback<Unit>) {
        PostsApi.retrofitService.removeById(id)
            .enqueue(object  :Callback<Unit>{
                override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                    if (!response.isSuccessful) {
                        callback.onError(RuntimeException(response.message()))
                        return
                    }
                    callback.onSuccess(response.body() ?: throw RuntimeException("body is null"))
                }
                override fun onFailure(call: Call<Unit>, t: Throwable) {
                    TODO("Not yet implemented")
                }

            })
    }
}
