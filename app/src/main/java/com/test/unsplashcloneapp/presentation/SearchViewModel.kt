package com.test.unsplashcloneapp.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.test.unsplashcloneapp.data.remote.UnsplashPagingSource
import com.test.unsplashcloneapp.data.remote.UnsplashService
import com.test.unsplashcloneapp.model.UnsplashPhoto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val unsplashService: UnsplashService
) : ViewModel() {

    private val _query = MutableStateFlow("nature") // 초기 검색어 (빈 값이어도 됨)

    @OptIn(ExperimentalCoroutinesApi::class)
    val photos: Flow<PagingData<UnsplashPhoto>> = _query.flatMapLatest { query ->
        Pager(
            config = PagingConfig(pageSize = 30, enablePlaceholders = false),
            pagingSourceFactory = { UnsplashPagingSource(unsplashService, query) }
        ).flow
    }.cachedIn(viewModelScope)

    fun search(newQuery: String) {
        if (newQuery.isNotEmpty()) {
            _query.value = newQuery
        }
    }
}