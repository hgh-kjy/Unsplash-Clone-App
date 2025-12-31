package com.test.unsplashcloneapp.presentation.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import com.test.unsplashcloneapp.data.local.BookmarkEntity
import com.test.unsplashcloneapp.presentation.BookmarkViewModel
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test

class BookmarkScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val mockViewModel = mockk<BookmarkViewModel>(relaxed = true)

    @Test
    fun bookmark_screen_displays_empty_message_when_no_bookmarks() {
        // Given: 빈 리스트 반환
        every { mockViewModel.bookmarks } returns MutableStateFlow(emptyList())

        // When
        composeRule.setContent {
            BookmarkScreen(
                onBackClick = {},
                onImageClick = {},
                viewModel = mockViewModel
            )
        }

        // Then: "Empty." 텍스트 확인
        composeRule.onNodeWithText("Empty.").assertIsDisplayed()
    }

    @Test
    fun bookmark_screen_displays_grid_items_when_bookmarks_exist() {
        // Given: 북마크 2개 준비
        val bookmarks = listOf(
            BookmarkEntity("1", "url1", "user1", 100, 100, "date"),
            BookmarkEntity("2", "url2", "user2", 100, 100, "date")
        )
        every { mockViewModel.bookmarks } returns MutableStateFlow(bookmarks)

        // When
        composeRule.setContent {
            BookmarkScreen(
                onBackClick = {},
                onImageClick = {},
                viewModel = mockViewModel
            )
        }

        // Then
        // 1. "Empty." 텍스트가 없어야 함
        composeRule.onNodeWithText("Empty.").assertDoesNotExist()

        // 2. 이미지가 2개 표시되어야 함 (이미지 ContentDescription으로 확인)
        // LazyVerticalGrid는 화면에 보이는 만큼만 그리기 때문에, 아이템이 적을 때는 모두 찾을 수 있음
        // 이미지의 contentDescription이 "Bookmarked Image"로 설정되어 있음
        val images = composeRule.onAllNodesWithContentDescription("Bookmarked Image")

        // Assert: 노드 개수가 2개인지 확인
        assert(images.fetchSemanticsNodes().size == 2)
    }
}