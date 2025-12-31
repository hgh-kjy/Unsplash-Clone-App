package com.test.unsplashcloneapp.presentation.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.test.unsplashcloneapp.presentation.BookmarkViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookmarkScreen(
    onBackClick: () -> Unit,
    onImageClick: (String) -> Unit,
    viewModel: BookmarkViewModel = hiltViewModel()
) {
    val bookmarks by viewModel.bookmarks.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Bookmarks") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        if (bookmarks.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = androidx.compose.ui.Alignment.Center) {
                Text("Empty.")
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(padding)
            ) {
                items(bookmarks) { bookmark ->
                    AsyncImage(
                        model = bookmark.imageUrl,
                        contentDescription = "Bookmarked Image",
                        modifier = Modifier
                            .aspectRatio(1f)
                            .clickable { onImageClick(bookmark.id) },
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }
    }
}