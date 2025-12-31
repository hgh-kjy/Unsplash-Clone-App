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
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class BookmarkViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: BookmarkViewModel
    private val dao: BookmarkDao = mockk()

    @Test
    fun bookmarks_state_flow_emits_data_from_dao() = runTest {
        // Given (가짜 데이터 준비)
        val fakeBookmarks = listOf(
            BookmarkEntity("1", "url1", "author1", 100, 100, "2024-01-01"),
            BookmarkEntity("2", "url2", "author2", 200, 200, "2024-01-02")
        )

        // DAO가 호출되면 가짜 데이터 Flow를 리턴하도록 설정
        every { dao.getAllBookmarks() } returns flowOf(fakeBookmarks)

        // When (ViewModel 생성 시점에 데이터를 불러옴)
        viewModel = BookmarkViewModel(dao)

        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.bookmarks.collect {}
        }

        // Then (StateFlow 값 검증)
        // stateIn을 사용했으므로 초기값 혹은 수집된 값을 확인
        // (StateFlow는 구독자가 없어도 최신 값을 가집니다)
        val result = viewModel.bookmarks.value

        assertEquals(2, result.size)
        assertEquals("author1", result[0].author)
        assertEquals("author2", result[1].author)

        // DAO가 실제로 호출되었는지 확인
        verify(exactly = 1) { dao.getAllBookmarks() }
    }

    @Test
    fun bookmarks_state_flow_is_empty_initially() = runTest {
        // Given (빈 데이터)
        every { dao.getAllBookmarks() } returns flowOf(emptyList())

        // When
        viewModel = BookmarkViewModel(dao)

        // Then
        val result = viewModel.bookmarks.value
        assertEquals(0, result.size)
    }
}