package com.test.unsplashcloneapp.presentation

import com.test.unsplashcloneapp.data.local.BookmarkDao
import com.test.unsplashcloneapp.data.remote.UnsplashService
import com.test.unsplashcloneapp.model.PhotoUrls
import com.test.unsplashcloneapp.model.UnsplashPhoto
import com.test.unsplashcloneapp.model.User
import com.test.unsplashcloneapp.util.MainDispatcherRule
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SearchViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val unsplashService: UnsplashService = mockk()
    private val bookmarkDao: BookmarkDao = mockk(relaxed = true)

    private lateinit var viewModel: SearchViewModel

    private val fakePhoto1 = UnsplashPhoto(
        id = "1", createdAt = "2024-01-01", width = 100, height = 100, color = "#000",
        description = "desc", urls = PhotoUrls("", "", "", "", ""),
        user = User("u1", "user1", "Name1")
    )
    private val fakePhoto2 = UnsplashPhoto(
        id = "2", createdAt = "2024-01-02", width = 100, height = 100, color = "#000",
        description = "desc", urls = PhotoUrls("", "", "", "", ""),
        user = User("u2", "user2", "Name2")
    )

    @Test
    fun initial_state_is_default_query() = runTest {
        viewModel = SearchViewModel(unsplashService, bookmarkDao)

        assertEquals(SearchViewModel.DEFAULT_QUERY, viewModel.searchQuery.value)
        assertTrue(viewModel.isDefaultState())
    }

    @Test
    fun search_updates_searchQuery_only_when_text_is_not_empty() = runTest {
        viewModel = SearchViewModel(unsplashService, bookmarkDao)

        viewModel.updateTextQuery("mountain")

        viewModel.search()

        assertEquals("mountain", viewModel.searchQuery.value)
        assertFalse(viewModel.isDefaultState())
    }

    @Test
    fun resetToDefault_clears_text_and_selection_and_resets_query() = runTest {
        viewModel = SearchViewModel(unsplashService, bookmarkDao)

        viewModel.updateTextQuery("forest")
        viewModel.search()
        viewModel.toggleSelection(fakePhoto1)

        viewModel.resetToDefault()

        assertEquals("", viewModel.textQuery.value)
        assertEquals(SearchViewModel.DEFAULT_QUERY, viewModel.searchQuery.value)
        assertTrue(viewModel.selectedPhotos.value.isEmpty())
    }

    @Test
    fun toggleSelection_adds_and_removes_photos() = runTest {
        viewModel = SearchViewModel(unsplashService, bookmarkDao)

        viewModel.toggleSelection(fakePhoto1)
        assertTrue(viewModel.selectedPhotos.value.containsKey("1"))
        assertEquals(1, viewModel.selectedPhotos.value.size)

        viewModel.toggleSelection(fakePhoto2)
        assertTrue(viewModel.selectedPhotos.value.containsKey("2"))
        assertEquals(2, viewModel.selectedPhotos.value.size)

        viewModel.toggleSelection(fakePhoto1)
        assertFalse(viewModel.selectedPhotos.value.containsKey("1"))
        assertEquals(1, viewModel.selectedPhotos.value.size)
    }

    @Test
    fun saveSelectedToBookmarks_inserts_data_to_dao_and_clears_selection() = runTest {
        viewModel = SearchViewModel(unsplashService, bookmarkDao)

        viewModel.toggleSelection(fakePhoto1)
        viewModel.toggleSelection(fakePhoto2)

        viewModel.saveSelectedToBookmarks()

        coVerify { bookmarkDao.insertBookmarks(any()) }

        assertTrue(viewModel.selectedPhotos.value.isEmpty())
    }
}