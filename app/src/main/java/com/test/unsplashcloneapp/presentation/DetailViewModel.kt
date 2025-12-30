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
                // 상세 정보를 API로 다시 불러오거나, 이전 화면에서 전달받은 데이터를 사용해도 됩니다.
                // 여기서는 API를 호출하여 최신 정보를 가져오는 방식으로 구현합니다.
                val photo = unsplashService.getPhotoDetail(id)
                _photoDetail.value = photo
            } catch (e: Exception) {
                // 에러 처리 (로그 등)
                e.printStackTrace()
            }
        }
    }

    // 현재 보고 있는 사진이 북마크 되어있는지 확인
    fun isBookmarked(id: String): Flow<Boolean> = bookmarkDao.isBookmarked(id)

    fun toggleBookmark(photo: UnsplashPhoto, isCurrentlyBookmarked: Boolean) {
        viewModelScope.launch {
            if (isCurrentlyBookmarked) {
                bookmarkDao.deleteBookmark(photo.id)
            } else {
                val entity = BookmarkEntity(
                    id = photo.id,
                    imageUrl = photo.urls.regular, // 또는 small
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