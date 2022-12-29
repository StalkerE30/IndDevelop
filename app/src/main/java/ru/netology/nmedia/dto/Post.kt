package ru.netology.nmedia.dto

data class Media(val id:String)

data class Post(
    val id: Long,
    val author: String,
    val authorAvatar:String,
    val content: String,
    val published: String,
    val likedByMe: Boolean,
    val likes: Int = 0,
    val show: Boolean,
    val attachment: Attachment? = null
)

data class Attachment(
    val uri: String,
    val type: AttachmentType,
)

enum class AttachmentType{
    IMAGE
}