package com.test.unsplashcloneapp.presentation.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.test.unsplashcloneapp.R
import com.test.unsplashcloneapp.presentation.BookmarkViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun BookmarkScreen(
    onBackClick: () -> Unit,
    onImageClick: (String) -> Unit,
    viewModel: BookmarkViewModel = hiltViewModel()
) {
    val bookmarks by viewModel.bookmarks.collectAsState()
    val selectedIds by viewModel.selectedIds.collectAsState()
    val isSelectionMode = selectedIds.isNotEmpty()

    BackHandler(enabled = isSelectionMode) {
        viewModel.clearSelection()
    }

    Scaffold(
        topBar = {
            if (isSelectionMode) {
                TopAppBar(
                    title = { Text(stringResource(R.string.msg_selected_count, selectedIds.size)) },
                    navigationIcon = {
                        IconButton(onClick = { viewModel.clearSelection() }) {
                            Icon(Icons.Default.Close, contentDescription = stringResource(R.string.desc_close))
                        }
                    },
                    actions = {
                        IconButton(onClick = { viewModel.deleteSelectedBookmarks() }) {
                            Icon(Icons.Default.Delete, contentDescription = stringResource(R.string.desc_delete))
                        }
                    }
                )
            } else {
                TopAppBar(
                    title = { Text(stringResource(R.string.bookmarks_title)) },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.desc_back))
                        }
                    }
                )
            }
        }
    ) { padding ->
        if (bookmarks.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text(stringResource(R.string.empty_bookmarks))
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(8.dp),
                modifier = Modifier.padding(padding).fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(bookmarks) { item ->
                    val isSelected = selectedIds.contains(item.id)
                    Box(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .combinedClickable(
                                onClick = {
                                    if (isSelectionMode) {
                                        viewModel.toggleSelection(item.id)
                                    } else {
                                        onImageClick(item.id)
                                    }
                                },
                                onLongClick = {
                                    viewModel.toggleSelection(item.id)
                                }
                            )
                            .border(
                                width = if (isSelected) 3.dp else 0.dp,
                                color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent
                            )
                    ) {
                        AsyncImage(
                            model = item.imageUrl,
                            contentDescription = stringResource(R.string.desc_photo),
                            modifier = Modifier.fillMaxSize().alpha(if (isSelected) 0.5f else 1f),
                            contentScale = ContentScale.Crop
                        )
                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = stringResource(R.string.desc_selected),
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.align(Alignment.TopEnd).padding(8.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}