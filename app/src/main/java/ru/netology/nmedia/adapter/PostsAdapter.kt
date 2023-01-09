package ru.netology.nmedia.adapter

import android.view.View
import androidx.core.net.toUri
import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.CardPostBinding
import ru.netology.nmedia.dto.Post
import com.bumptech.glide.Glide
import ru.netology.nmedia.BuildConfig

interface OnInteractionListener {
    fun onLike(post: Post) {}
    fun onEdit(post: Post) {}
    fun onRemove(post: Post) {}
    fun onShare(post: Post) {}
    fun onClickPhoto(url: String) {}
}

class PostsAdapter(
    private val onInteractionListener: OnInteractionListener,
) : ListAdapter<Post, PostViewHolder>(PostDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = CardPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(binding, onInteractionListener)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = getItem(position)
        holder.bind(post)
    }
}

class PostViewHolder(
    private val binding: CardPostBinding,
    private val onInteractionListener: OnInteractionListener,
) : RecyclerView.ViewHolder(binding.root) {

    @SuppressLint("SuspiciousIndentation")
    fun bind(post: Post) {
        binding.apply {
            author.text = post.author
            published.text = post.published
            val nameFileAvatar=post.authorAvatar
            //val urlImageAvatar = "http://192.168.102.171:9999/avatars/${nameFileAvatar}"
            val urlImageAvatar = "${BuildConfig.BASE_URL}/avatars/${nameFileAvatar}"
                Glide.with(binding.avatar)
                    .load(urlImageAvatar)
                    .placeholder(R.drawable.ic_loading_100dp)
                    .error(R.drawable.ic_error_100dp)
                    .timeout(10_000)
                    .circleCrop()
                    .into(avatar)
            content.text = post.content
            // в адаптере
            like.isChecked = post.likedByMe
            like.text = "${post.likes}"

            if (post.attachment?.uri == null) {
                binding.photoContainer.isGone = true
            }   else {
                binding.photoContainer.isVisible = true
                val urlImagePost = "${BuildConfig.BASE_URL}/media/${post.attachment.uri}"
                Glide.with(binding.photo)
                    .load(urlImagePost)
                    .placeholder(R.drawable.ic_loading_100dp)
                    .timeout(10_000)
                    .circleCrop()
                    .into(photo)
            }

            photo.setOnClickListener{
                if (post.attachment?.uri != null) {
                    val urlImagePost = "${BuildConfig.BASE_URL}/media/${post.attachment.uri}"
                    onInteractionListener.onClickPhoto(urlImagePost)
                }
            }

            menu.setOnClickListener {
                PopupMenu(it.context, it).apply {
                    inflate(R.menu.options_post)
                    setOnMenuItemClickListener { item ->
                        when (item.itemId) {
                            R.id.remove -> {
                                onInteractionListener.onRemove(post)
                                true
                            }
                            R.id.edit -> {
                                onInteractionListener.onEdit(post)
                                true
                            }

                            else -> false
                        }
                    }
                }.show()
            }

            like.setOnClickListener {
                onInteractionListener.onLike(post)
            }

            share.setOnClickListener {
                onInteractionListener.onShare(post)
            }
        }
    }
}

class PostDiffCallback : DiffUtil.ItemCallback<Post>() {
    override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem == newItem
    }
}
