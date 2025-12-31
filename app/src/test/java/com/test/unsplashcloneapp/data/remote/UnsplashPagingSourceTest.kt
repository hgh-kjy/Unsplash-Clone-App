package com.test.unsplashcloneapp.data.remote

import androidx.paging.PagingSource
import com.test.unsplashcloneapp.model.PhotoUrls
import com.test.unsplashcloneapp.model.SearchResponse
import com.test.unsplashcloneapp.model.UnsplashPhoto
import com.test.unsplashcloneapp.model.User
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import java.io.IOException

class UnsplashPagingSourceTest {

    private val api: UnsplashService = mockk()

    private fun createFakePhotos(): List<UnsplashPhoto> {
        return listOf(
            UnsplashPhoto("1", "2024-01-01", 100, 100, "#000", "desc", PhotoUrls("","","","",""), User("u1", "user1", "Name1")),
            UnsplashPhoto("2", "2024-01-01", 100, 100, "#000", "desc", PhotoUrls("","","","",""), User("u2", "user2", "Name2"))
        )
    }

    @Test
    fun load_returns_page_when_on_successful_load_of_item_keyed_data() = runTest {
        val fakePhotos = createFakePhotos()
        val fakeResponse = SearchResponse(results = fakePhotos, totalPages = 10)

        coEvery { api.searchPhotos(any(), any(), any()) } returns fakeResponse

        val pagingSource = UnsplashPagingSource(api, "query")

        val actual = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = 1,
                loadSize = 2,
                placeholdersEnabled = false
            )
        )

        val expected = PagingSource.LoadResult.Page(
            data = fakePhotos,
            prevKey = null,
            nextKey = 2
        )

        assertEquals(expected, actual)
    }

    @Test
    fun load_returns_error_when_api_fails() = runTest {
        val exception = IOException("Network Error")
        coEvery { api.searchPhotos(any(), any(), any()) } throws exception

        val pagingSource = UnsplashPagingSource(api, "query")

        val actual = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = 1,
                loadSize = 2,
                placeholdersEnabled = false
            )
        )

        assert(actual is PagingSource.LoadResult.Error)
        assertEquals(exception, (actual as PagingSource.LoadResult.Error).throwable)
    }
}