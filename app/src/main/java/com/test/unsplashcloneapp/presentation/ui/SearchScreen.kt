package com.test.unsplashcloneapp.presentation.ui

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.test.unsplashcloneapp.R
import com.test.unsplashcloneapp.presentation.SearchViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun SearchScreen(
    onImageClick: (String) -> Unit,
    onBookmarkClick: () -> Unit,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val photos = viewModel.photos.collectAsLazyPagingItems()
    val textQuery by viewModel.textQuery.collectAsState()
    val selectedPhotos by viewModel.selectedPhotos.collectAsState()
    val isSelectionMode = selectedPhotos.isNotEmpty()

    val keyboardController = LocalSoftwareKeyboardController.current
    val context = LocalContext.current
    var backPressedTime by remember { mutableLongStateOf(0L) }

    val msgPressBack = stringResource(R.string.msg_press_back_again)
    val msgSaved = stringResource(R.string.msg_saved)

    BackHandler {
        when {
            isSelectionMode -> viewModel.clearSelection()
            !viewModel.isDefaultState() || textQuery.isNotEmpty() -> viewModel.resetToDefault()
            else -> {
                val currentTime = System.currentTimeMillis()
                if (currentTime - backPressedTime < 2000) {
                    (context as? Activity)?.finish()
                } else {
                    backPressedTime = currentTime
                    Toast.makeText(context, msgPressBack, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    Scaffold(
        topBar = {
            if (isSelectionMode) {
                TopAppBar(
                    title = { Text(stringResource(R.string.msg_selected_count, selectedPhotos.size)) },
                    navigationIcon = {
                        IconButton(onClick = { viewModel.clearSelection() }) {
                            Icon(Icons.Default.Close, contentDescription = stringResource(R.string.desc_close_selection))
                        }
                    },
                    actions = {
                        IconButton(onClick = {
                            viewModel.saveSelectedToBookmarks()
                            Toast.makeText(context, msgSaved, Toast.LENGTH_SHORT).show()
                        }) {
                            Icon(Icons.Default.BookmarkAdd, contentDescription = stringResource(R.string.desc_save_selection))
                        }
                    }
                )
            } else {
                TopAppBar(
                    title = { Text(stringResource(R.string.search_title)) },
                    actions = {
                        IconButton(onClick = onBookmarkClick) {
                            Icon(Icons.Default.CollectionsBookmark, contentDescription = stringResource(R.string.desc_bookmark))
                        }
                    }
                )
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            OutlinedTextField(
                value = textQuery,
                onValueChange = { viewModel.updateTextQuery(it) },
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                placeholder = { Text(stringResource(R.string.search_placeholder)) },
                trailingIcon = {
                    IconButton(onClick = {
                        viewModel.search()
                        keyboardController?.hide()
                    }) {
                        Icon(Icons.Default.Search, contentDescription = stringResource(R.string.desc_search))
                    }
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = {
                    viewModel.search()
                    keyboardController?.hide()
                })
            )

            Box(modifier = Modifier.fillMaxSize()) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(photos.itemCount) { index ->
                        val photo = photos[index]
                        photo?.let {
                            val isSelected = selectedPhotos.containsKey(it.id)

                            Box(
                                modifier = Modifier
                                    .aspectRatio(1f)
                                    .combinedClickable(
                                        onClick = {
                                            if (isSelectionMode) {
                                                viewModel.toggleSelection(it)
                                            } else {
                                                onImageClick(it.id)
                                            }
                                        },
                                        onLongClick = {
                                            viewModel.toggleSelection(it)
                                        }
                                    )
                                    .border(
                                        width = if (isSelected) 3.dp else 0.dp,
                                        color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent
                                    )
                            ) {
                                AsyncImage(
                                    model = it.urls.small,
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

                when {
                    photos.loadState.refresh is LoadState.Loading -> {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    }
                    photos.loadState.refresh is LoadState.Error -> {
                        val e = (photos.loadState.refresh as LoadState.Error).error
                        Column(
                            modifier = Modifier.align(Alignment.Center),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = stringResource(R.string.msg_error_prefix) + e.localizedMessage)
                            Button(onClick = { photos.retry() }) {
                                Text(stringResource(R.string.label_retry))
                            }
                        }
                    }
                    photos.loadState.refresh is LoadState.NotLoading && photos.itemCount == 0 -> {
                        Text(
                            text = stringResource(R.string.msg_no_result),
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }
            }
        }
    }
}