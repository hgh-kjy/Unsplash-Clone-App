package com.test.unsplashcloneapp.presentation.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CollectionsBookmark
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.test.unsplashcloneapp.presentation.SearchViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    onImageClick: (String) -> Unit,
    onBookmarkClick: () -> Unit,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val photos = viewModel.photos.collectAsLazyPagingItems()
    var searchQuery by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Unsplash Clone") },
                actions = {
                    IconButton(onClick = onBookmarkClick) {
                        Icon(imageVector = Icons.Default.CollectionsBookmark, contentDescription = "Bookmarks")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            // 검색바
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder = { Text("Search photos...") },
                trailingIcon = {
                    IconButton(onClick = { viewModel.search(searchQuery) }) {
                        Icon(imageVector = Icons.Default.Search, contentDescription = "Search")
                    }
                },
                singleLine = true
            )

            // 이미지 그리드 리스트
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
                        AsyncImage(
                            model = it.urls.small,
                            contentDescription = it.description,
                            modifier = Modifier
                                .aspectRatio(1f)
                                .clickable { onImageClick(it.id) },
                            contentScale = ContentScale.Crop
                        )
                    }
                }

                // 로딩 상태 표시
                photos.apply {
                    when {
                        loadState.append is LoadState.Loading -> {
                            item { CircularProgressIndicator(modifier = Modifier.padding(16.dp)) }
                        }
                        loadState.refresh is LoadState.Loading -> {
                            item { CircularProgressIndicator(modifier = Modifier.padding(16.dp)) }
                        }
                        loadState.refresh is LoadState.Error -> {
                            // 에러 처리 (생략)
                        }
                    }
                }
            }
        }
    }
}