package com.test.unsplashcloneapp.presentation

import com.test.unsplashcloneapp.data.local.BookmarkDao
import com.test.unsplashcloneapp.data.local.BookmarkEntity
import com.test.unsplashcloneapp.util.MainDispatcherRule
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class BookmarkViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: BookmarkViewModel
    private val dao: BookmarkDao = mockk()

    @Test
    fun bookmarks_state_flow_emits_data_from_dao() = runTest {
        val fakeBookmarks = listOf(
            BookmarkEntity("1", "url1", "author1", 100, 100, "2024-01-01"),
            BookmarkEntity("2", "url2", "author2", 200, 200, "2024-01-02")
        )

        every { dao.getAllBookmarks() } returns flowOf(fakeBookmarks)

        viewModel = BookmarkViewModel(dao)

        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.bookmarks.collect {}
        }

        val result = viewModel.bookmarks.value

        assertEquals(2, result.size)
        assertEquals("author1", result[0].author)
        assertEquals("author2", result[1].author)

        verify(exactly = 1) { dao.getAllBookmarks() }
    }

    @Test
    fun bookmarks_state_flow_is_empty_initially() = runTest {
        every { dao.getAllBookmarks() } returns flowOf(emptyList())

        viewModel = BookmarkViewModel(dao)

        val result = viewModel.bookmarks.value
        assertEquals(0, result.size)
    }
    @Test
    fun toggleSelection_adds_and_removes_id_correctly() = runTest {
        viewModel = BookmarkViewModel(dao)

        viewModel.toggleSelection("photo1")

        assert(viewModel.selectedIds.value.contains("photo1"))

        viewModel.toggleSelection("photo1")

        assert(!viewModel.selectedIds.value.contains("photo1"))
    }

    @Test
    fun deleteSelectedBookmarks_calls_dao_and_clears_selection() = runTest {
        viewModel = BookmarkViewModel(dao)
        viewModel.toggleSelection("1")
        viewModel.toggleSelection("2")

        viewModel.deleteSelectedBookmarks()

        io.mockk.coVerify {
            dao.deleteBookmarks(match { it.containsAll(listOf("1", "2")) && it.size == 2 })
        }

        assert(viewModel.selectedIds.value.isEmpty())
    }
}