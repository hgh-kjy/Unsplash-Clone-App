package com.test.unsplashcloneapp.presentation.ui

import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.test.unsplashcloneapp.R
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
    val context = LocalContext.current
    val msgAdded = stringResource(R.string.msg_bookmark_added)
    val msgRemoved = stringResource(R.string.msg_bookmark_removed)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.detail_title)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.desc_back)
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    photo?.let {
                        viewModel.toggleBookmark(it, isBookmarked)
                        val message = if (isBookmarked) msgRemoved else msgAdded
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                    }
                }
            ) {
                Icon(
                    imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                    contentDescription = stringResource(R.string.desc_bookmark)
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

                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        DetailRow(
                            label = stringResource(R.string.label_id),
                            value = item.id
                        )

                        HorizontalDivider(color = Color.Gray.copy(alpha = 0.5f))
                        DetailRow(
                            label = stringResource(R.string.label_author),
                            value = item.user.name,
                        )

                        HorizontalDivider(color = Color.Gray.copy(alpha = 0.5f))

                        DetailRow(
                            label = stringResource(R.string.label_size),
                            value = "${item.width} x ${item.height}"
                        )

                        HorizontalDivider(color = Color.Gray.copy(alpha = 0.5f))

                        DetailRow(
                            label = stringResource(R.string.label_created_at),
                            value = item.createdAt.substringBefore("T")
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        } ?: Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DetailRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )

        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.onSurface
            ),
            modifier = Modifier.padding(start = 16.dp)
        )
    }
}