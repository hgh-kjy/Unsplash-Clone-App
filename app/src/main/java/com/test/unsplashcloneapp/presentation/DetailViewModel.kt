package com.test.unsplashcloneapp.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.test.unsplashcloneapp.data.local.BookmarkDao
import com.test.unsplashcloneapp.data.local.BookmarkEntity
import com.test.unsplashcloneapp.data.remote.UnsplashService
import com.test.unsplashcloneapp.model.UnsplashPhoto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val unsplashService: UnsplashService,
    private val bookmarkDao: BookmarkDao
) : ViewModel() {

    private val _photoDetail = MutableStateFlow<UnsplashPhoto?>(null)
    val photoDetail: StateFlow<UnsplashPhoto?> = _photoDetail

    fun loadPhotoDetail(id: String) {
        viewModelScope.launch {
            try {
                val photo = unsplashService.getPhotoDetail(id)
                _photoDetail.value = photo
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun isBookmarked(id: String): Flow<Boolean> = bookmarkDao.isBookmarked(id)

    fun toggleBookmark(photo: UnsplashPhoto, isCurrentlyBookmarked: Boolean) {
        viewModelScope.launch {
            if (isCurrentlyBookmarked) {
                bookmarkDao.deleteBookmark(photo.id)
            } else {
                val entity = BookmarkEntity(
                    id = photo.id,
                    imageUrl = photo.urls.regular,
                    author = photo.user.name,
                    width = photo.width,
                    height = photo.height,
                    createdAt = photo.createdAt
                )
                bookmarkDao.insertBookmark(entity)
            }
        }
    }
}