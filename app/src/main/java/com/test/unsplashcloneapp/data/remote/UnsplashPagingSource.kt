package com.test.unsplashcloneapp.data.remote

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.test.unsplashcloneapp.model.UnsplashPhoto
import retrofit2.HttpException
import java.io.IOException

class UnsplashPagingSource(
    private val service: UnsplashService,
    private val query: String
) : PagingSource<Int, UnsplashPhoto>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, UnsplashPhoto> {
        val position = params.key ?: 1
        return try {
            val response = service.searchPhotos(
                query = query,
                page = position,
                perPage = params.loadSize
            )
            val photos = response.results

            LoadResult.Page(
                data = photos,
                prevKey = if (position == 1) null else position - 1,
                nextKey = if (photos.isEmpty()) null else position + 1
            )
        } catch (exception: IOException) {
            LoadResult.Error(exception)
        } catch (exception: HttpException) {
            LoadResult.Error(exception)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, UnsplashPhoto>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}