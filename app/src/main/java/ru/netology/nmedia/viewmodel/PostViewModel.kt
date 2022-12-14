package ru.netology.nmedia.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ru.netology.nmedia.db.AppDb
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.model.FeedModel
import ru.netology.nmedia.model.FeedModelState
import ru.netology.nmedia.repository.*
import ru.netology.nmedia.util.SingleLiveEvent
import ru.netology.nmedia.model.PhotoModel
import java.io.File

private val empty = Post(
    id = 0,
    content = "",
    author = "",
    authorAvatar = "",
    likedByMe = false,
    likes = 0,
    published = "",
    show = false
)

private val noPhoto = PhotoModel()

class PostViewModel(application: Application) : AndroidViewModel(application) {
    // упрощённый вариант
    private val repository: PostRepository = PostRepositoryImpl(AppDb.getInstance(context = application).postDao())
    val data: LiveData<FeedModel> = repository.data.map(::FeedModel)
        .asLiveData(Dispatchers.Default)

    val newerCount:LiveData<Int> = data.switchMap {
        val newerId = it.posts.firstOrNull()?.id ?:0L
        repository.getNewerCount(newerId)
            .asLiveData()
    }
    private val _dataState = MutableLiveData<FeedModelState>(FeedModelState.Idle)
    val dataState: LiveData<FeedModelState>
        //get() = repository.data.map{FeedModel(it,it.isEmpty())}
        get() = _dataState

    private val _photo = MutableLiveData(noPhoto)
    val photo: LiveData<PhotoModel>
        get() = _photo

    init {
        loadPosts()
    }

    private val edited = MutableLiveData(empty)
    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit>
        get() = _postCreated

    init {
        loadPosts()
    }

    fun refreshPosts() = viewModelScope.launch {
        try {
            _dataState.value = FeedModelState.Refreshing
            repository.getAll()
            _dataState.value = FeedModelState.Idle
        } catch (e: Exception) {
            _dataState.value = FeedModelState.Error
        }
    }

    fun update() = viewModelScope.launch {
        try {
            _dataState.value = FeedModelState.Refreshing
            repository.update()
            _dataState.value = FeedModelState.Idle
        } catch (e: Exception) {
            _dataState.value = FeedModelState.Error
        }
    }

    fun loadPosts()=viewModelScope.launch {
        _dataState.value= FeedModelState.Loading
        try{
            repository.getAll()
            _dataState.value = FeedModelState.Idle
        } catch (e: Exception) {
            _dataState.value = FeedModelState.Error
        }
    }


    fun save() {
        edited.value?.let {
            _postCreated.value = Unit
            viewModelScope.launch {
                try {
                    when (_photo.value) {
                        noPhoto ->repository.save(it)
                        else -> _photo.value?.file?.let { file ->
                        repository.saveWithAttachment(it, file)
                        }
                    }
                    _dataState.value = FeedModelState.Idle
                } catch (e: Exception) {
                    _dataState.value = FeedModelState.Error
                }
            }
        }
        edited.value = empty
    }

    fun edit(post: Post) {
        edited.value = post
    }

    fun changeContent(content: String) {
        val text = content.trim()
        if (edited.value?.content == text) {
            return
        }
        edited.value = edited.value?.copy(content = text)
    }

    fun likeById(id: Long,likeUnlike:Boolean) = viewModelScope.launch{
        try {
            repository.likeById(id, likeUnlike)
            _dataState.value = FeedModelState.Idle
        }catch (e:Exception){
            _dataState.value = FeedModelState.Error
        }
    }

    fun removeById(id: Long) = viewModelScope.launch{
        try {
            repository.removeById(id)
            _dataState.value = FeedModelState.Idle
        }catch (e:Exception){
            _dataState.value = FeedModelState.Error
        }
    }

    fun changePhoto(uri: Uri?, file: File?) {
        _photo.value = PhotoModel(uri, file)
    }


}
