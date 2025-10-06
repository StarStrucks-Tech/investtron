package com.example.krishna_project.ui.screens

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.krishna_project.data.local.Post
import com.example.krishna_project.ui.viewmodel.DashboardViewModel
import kotlinx.coroutines.launch
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource

@Composable
fun PostDialog(onDismiss: () -> Unit, onConfirm: (Post) -> Unit, postToEdit: Post? = null) {
    var title by remember { mutableStateOf(postToEdit?.title ?: "") }
    var subtitle by remember { mutableStateOf(postToEdit?.subtitle ?: "") }
    var description by remember { mutableStateOf(postToEdit?.description ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (postToEdit == null) "Add a new post" else "Edit post") },
        text = {
            Column {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Title") })
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = subtitle, onValueChange = { subtitle = it }, label = { Text("Subtitle") })
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Description") })
            }
        },
        confirmButton = {
            Button(onClick = {
                onConfirm(
                    postToEdit?.copy(
                        title = title,
                        subtitle = subtitle,
                        description = description
                    ) ?: Post(title = title, subtitle = subtitle, description = description, author = "", authorRole = "")
                )
            }) {
                Text(if (postToEdit == null) "Add" else "Save Changes")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(onLogout: () -> Unit, viewModel: DashboardViewModel = hiltViewModel()) {
    val posts by viewModel.posts.collectAsState(initial = emptyList())
    val userName by viewModel.userName.collectAsState()
    val comments by viewModel.selectedPostComments.collectAsState()

    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var selectedPost by remember { mutableStateOf<Post?>(null) }
    var showAddPostDialog by remember { mutableStateOf(false) }
    var showEditPostDialog by remember { mutableStateOf(false) }
    var postToEdit by remember { mutableStateOf<Post?>(null) }
    var showDeleteConfirmationDialog by remember { mutableStateOf(false) }
    var postToDelete by remember { mutableStateOf<Post?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Welcome, $userName") },
                actions = {
                    IconButton(onClick = {
                        viewModel.logout()
                        onLogout()
                    }) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Logout")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddPostDialog = true }) {
                Icon(Icons.Filled.Add, "Add new post")
            }
        }
    ) { padding ->
        if(posts.isNotEmpty()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            items(posts) {
                PostItem(
                    post = it,
                    currentUserName = userName,
                    onCommentClick = {
                        selectedPost = it
                        viewModel.loadComments(it.id)
                        scope.launch { sheetState.show() }
                    },
                    onEditClick = {
                        scope.launch {
                            snackbarHostState.showSnackbar(
                                message = "Edit functionality not yet implemented for post ${it.title}",
                                duration = SnackbarDuration.Short
                            )
                        }
                    },
                    onDeleteClick = {
                        postToDelete = it
                        showDeleteConfirmationDialog = true
                    }
                )
            }
        } } else {
        NoPosts()
    }

        if (sheetState.isVisible) {
            ModalBottomSheet(
                onDismissRequest = { scope.launch { sheetState.hide() } },
                sheetState = sheetState
            ) {
                selectedPost?.let { post ->
                    CommentSection(
                        comments = comments, onAddComment = { commentText ->
                        viewModel.addComment(post.id, text = commentText)
                    })
                }
            }
        }

        if (showAddPostDialog) {
            PostDialog(
                onDismiss = { showAddPostDialog = false },
                onConfirm = {
                    viewModel.addPost(it.title, it.subtitle, it.description)
                    showAddPostDialog = false
                }
            )
        }

        if (showEditPostDialog) {
            PostDialog(
                onDismiss = { showEditPostDialog = false },
                onConfirm = {
                    viewModel.editPost(it)
                    showEditPostDialog = false
                    postToEdit = null
                },
                postToEdit = postToEdit
            )
        }

        if (showDeleteConfirmationDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteConfirmationDialog = false },
                title = { Text("Delete Post") },
                text = { Text("Are you sure you want to delete \"${postToDelete?.title}\"?") },
                confirmButton = {
                    Button(onClick = {
                        postToDelete?.let { post ->
                            viewModel.deletePost(post.id)
                            scope.launch {
                                snackbarHostState.showSnackbar(
                                    message = "Post \"${post.title}\" deleted",
                                    duration = SnackbarDuration.Short
                                )
                            }
                        }
                        showDeleteConfirmationDialog = false
                        postToDelete = null
                    }) {
                        Text("Delete")
                    }
                },
                dismissButton = {
                    Button(onClick = { showDeleteConfirmationDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
fun PostItem(
    post: Post,
    currentUserName: String,
    onCommentClick: () -> Unit,
    onEditClick: (Post) -> Unit,
    onDeleteClick: (Post) -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = post.title, style = MaterialTheme.typography.titleLarge)
                val authorRoleChar = post.authorRole.first().toString()
                Text(
                    text = authorRoleChar,
                    modifier = Modifier
                        .background(
                            color = when (authorRoleChar) {
                                "F" -> Color.Yellow.copy(alpha = 0.6f)
                                "I" -> Color.Green.copy(alpha = 0.6f)
                                else -> Color.Transparent
                            },
                            shape = RoundedCornerShape(50)
                        )
                        .padding(16.dp),
                    style = MaterialTheme.typography.titleLarge
                )
            }
            Text(text = "by ${post.author}", style = MaterialTheme.typography.bodySmall)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = post.subtitle, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = post.description, style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(onClick = onCommentClick) {
                    Text("Comment")
                }
                if (post.author == currentUserName) {
                    Row {
                        IconButton(onClick = { onEditClick(post) }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit Post")
                        }
                        IconButton(onClick = { onDeleteClick(post) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete Post")
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun NoPosts() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            RotatingComposeLogo()
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "No Startups available, Please Add One", style = MaterialTheme.typography.titleLarge)
        }
    }
}

@Composable
fun RotatingComposeLogo() {
    // Infinite transition for rotation
    val infiniteTransition = rememberInfiniteTransition()

    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    Icon(
        imageVector = Icons.Default.Settings, // any vector from material icons
        contentDescription = "Settings logo",
        tint = Color.Gray,
        modifier = Modifier
            .size(100.dp)
            .graphicsLayer {
                rotationZ = rotation
            }
    )
}

@Composable
fun CommentSection(comments: List<com.example.krishna_project.data.local.Comment>, onAddComment: (String) -> Unit) {
    var commentText by remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Comments", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))
        LazyColumn(modifier = Modifier.heightIn(max = 200.dp)) {
            items(comments) {
                Card(
                    shape = RoundedCornerShape(8.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 8.dp)
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = it.author,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontStyle = FontStyle.Italic
                                )
                            )
                            val authorRoleChar = it.authorRole.first().toString()
                            Text(
                                text = authorRoleChar,
                                modifier = Modifier
                                    .background(
                                        color = when (authorRoleChar) {
                                            "F" -> Color.Yellow.copy(alpha = 0.6f)
                                            "I" -> Color.Green.copy(alpha = 0.6f)
                                            else -> Color.Transparent
                                        },
                                        shape = RoundedCornerShape(50)
                                    )
                                    .padding(12.dp),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = it.text, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = commentText,
                onValueChange = { commentText = it },
                label = { Text("Add a comment") },
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = { 
                onAddComment(commentText)
                commentText = ""
            }) {
                Text("Add")
            }
        }
    }
}
