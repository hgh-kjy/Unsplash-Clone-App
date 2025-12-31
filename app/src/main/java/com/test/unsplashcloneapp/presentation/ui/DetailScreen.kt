package com.test.unsplashcloneapp.presentation.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.test.unsplashcloneapp.presentation.DetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    photoId: String?,
    onBackClick: () -> Unit,
    viewModel: DetailViewModel = hiltViewModel()
) {
    LaunchedEffect(photoId) {
        photoId?.let { viewModel.loadPhotoDetail(it) }
    }

    val photo by viewModel.photoDetail.collectAsState()
    val isBookmarked by viewModel.isBookmarked(photoId ?: "").collectAsState(initial = false)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detail") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    photo?.let { viewModel.toggleBookmark(it, isBookmarked) }
                }
            ) {
                Icon(
                    imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                    contentDescription = "Bookmark"
                )
            }
        }
    ) { padding ->
        photo?.let { item ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
            ) {
                AsyncImage(
                    model = item.urls.regular,
                    contentDescription = item.description,
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    contentScale = ContentScale.FillWidth
                )

                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "ID: ${item.id}", style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Author: ${item.user.name}", style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Size: ${item.width} x ${item.height}", style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Created At: ${item.createdAt}", style = MaterialTheme.typography.bodyLarge)
                }
            }
        } ?: Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }
}