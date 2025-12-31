package com.test.unsplashcloneapp.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.test.unsplashcloneapp.data.local.BookmarkDao
import com.test.unsplashcloneapp.data.local.BookmarkEntity
import com.test.unsplashcloneapp.data.remote.UnsplashPagingSource
import com.test.unsplashcloneapp.data.remote.UnsplashService
import com.test.unsplashcloneapp.model.UnsplashPhoto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val unsplashService: UnsplashService,
    private val bookmarkDao: BookmarkDao // [추가] DB 접근 필요
) : ViewModel() {

    companion object {
        const val DEFAULT_QUERY = "all"
    }

    private val _searchQuery = MutableStateFlow(DEFAULT_QUERY)
    val searchQuery = _searchQuery.asStateFlow()

    private val _textQuery = MutableStateFlow("")
    val textQuery = _textQuery.asStateFlow()

    private val _selectedPhotos = MutableStateFlow<Map<String, UnsplashPhoto>>(emptyMap())
    val selectedPhotos = _selectedPhotos.asStateFlow()

    val isSelectionMode = _selectedPhotos.flatMapLatest {
        kotlinx.coroutines.flow.flowOf(it.isNotEmpty())
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val photos: Flow<PagingData<UnsplashPhoto>> = _searchQuery.flatMapLatest { query ->
        Pager(
            config = PagingConfig(pageSize = 30, initialLoadSize = 30, enablePlaceholders = false),
            pagingSourceFactory = { UnsplashPagingSource(unsplashService, query) }
        ).flow
    }.cachedIn(viewModelScope)

    fun updateTextQuery(newText: String) {
        _textQuery.value = newText
    }

    fun search() {
        if (_textQuery.value.isNotEmpty()) {
            _searchQuery.value = _textQuery.value
        }
    }

    fun resetToDefault() {
        _textQuery.value = ""
        _searchQuery.value = DEFAULT_QUERY
        clearSelection()
    }

    fun isDefaultState(): Boolean = _searchQuery.value == DEFAULT_QUERY

    fun toggleSelection(photo: UnsplashPhoto) {
        val current = _selectedPhotos.value.toMutableMap()
        if (current.containsKey(photo.id)) {
            current.remove(photo.id)
        } else {
            current[photo.id] = photo
        }
        _selectedPhotos.value = current
    }

    fun clearSelection() {
        _selectedPhotos.value = emptyMap()
    }

    fun saveSelectedToBookmarks() {
        viewModelScope.launch {
            val bookmarks = _selectedPhotos.value.values.map { photo ->
                BookmarkEntity(
                    id = photo.id,
                    imageUrl = photo.urls.regular,
                    author = photo.user.name,
                    width = photo.width,
                    height = photo.height,
                    createdAt = photo.createdAt
                )
            }
            if (bookmarks.isNotEmpty()) {
                bookmarkDao.insertBookmarks(bookmarks)
                clearSelection()
            }
        }
    }
}