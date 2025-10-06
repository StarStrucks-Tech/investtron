package com.example.krishna_project.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.krishna_project.data.local.Comment
import com.example.krishna_project.data.local.Post
import com.example.krishna_project.data.repository.PostRepository
import com.example.krishna_project.session.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val postRepository: PostRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    val userName: StateFlow<String> = MutableStateFlow(sessionManager.getUserId() ?: "")
    val userRole: StateFlow<String> = MutableStateFlow(sessionManager.getUserRole() ?: "")

    val posts: Flow<List<Post>> = postRepository.getPosts()

    private val _selectedPostComments = MutableStateFlow<List<Comment>>(emptyList())
    val selectedPostComments: StateFlow<List<Comment>> = _selectedPostComments

    fun addPost(title: String, subtitle: String, description: String) {
        viewModelScope.launch {
            postRepository.insertPost(
                Post(
                    title = title,
                    subtitle = subtitle,
                    description = description,
                    author = userName.value,
                    authorRole = userRole.value
                )
            )
        }
    }

    fun addStartupPost() {
        viewModelScope.launch {
            postRepository.insertPost(
                Post(
                    title = "New Startup",
                    subtitle = "An innovative new venture",
                    description = "This is a description of a new and exciting startup idea.",
                    author = "User",
                    authorRole = "Entrepreneur"
                )
            )
        }
    }

    fun loadComments(postId: Int) {
        viewModelScope.launch {
            postRepository.getCommentsForPost(postId).collect {
                _selectedPostComments.value = it
            }
        }
    }

    fun addComment(postId: Int, text: String) {
        viewModelScope.launch {
            postRepository.insertComment(
                Comment(
                    postId = postId,
                    author = userName.value,
                    authorRole = userRole.value,
                    text = text
                )
            )
        }
    }

    fun logout() {
        sessionManager.clearSession()
    }

    fun deletePost(postId: Int) {
        viewModelScope.launch {
            postRepository.deletePost(postId)
        }
    }

    fun editPost(post: Post) {
        viewModelScope.launch {
            postRepository.updatePost(post)
        }
    }
}
