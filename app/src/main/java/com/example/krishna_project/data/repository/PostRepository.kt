package com.example.krishna_project.data.repository

import com.example.krishna_project.data.local.PostDao
import com.example.krishna_project.data.local.Post
import com.example.krishna_project.data.local.Comment
import com.example.krishna_project.data.local.User
import com.example.krishna_project.data.local.UserDao
import kotlinx.coroutines.flow.Flow
import java.security.MessageDigest
import javax.inject.Inject

class PostRepository @Inject constructor(private val postDao: PostDao, private val userDao: UserDao) {

    fun getPosts(): Flow<List<Post>> = postDao.getPosts()

    suspend fun insertPost(post: Post) {
        postDao.insertPost(post)
    }

    fun getCommentsForPost(postId: Int): Flow<List<Comment>> = postDao.getCommentsForPost(postId)

    suspend fun insertComment(comment: Comment) {
        postDao.insertComment(comment)
    }

    suspend fun deletePost(postId: Int) {
        postDao.deletePost(postId)
    }

    suspend fun updatePost(post: Post) {
        postDao.updatePost(post)
    }

    suspend fun insertUser(user: User) {
        userDao.insertUser(user)
    }

    suspend fun getUserById(userId: String): User? {
        return userDao.getUserById(userId)
    }

    fun getPasswordHash(password: String): String {
        val bytes = password.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return digest.fold("") { str, it -> str + "%02x".format(it) }
    }
}
