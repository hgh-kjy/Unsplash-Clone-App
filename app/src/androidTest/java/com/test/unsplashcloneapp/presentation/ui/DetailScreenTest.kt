package com.test.unsplashcloneapp.presentation.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import com.test.unsplashcloneapp.model.PhotoUrls
import com.test.unsplashcloneapp.model.UnsplashPhoto
import com.test.unsplashcloneapp.model.User
import com.test.unsplashcloneapp.presentation.DetailViewModel
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import org.junit.Rule
import org.junit.Test

class DetailScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    // Mock ViewModel 생성 (기능은 없지만 껍데기는 있는 가짜 객체)
    private val mockViewModel = mockk<DetailViewModel>(relaxed = true)

    @Test
    fun detail_screen_displays_photo_information() {
        // Given (가짜 데이터 준비)
        val fakePhoto = UnsplashPhoto(
            id = "photo_1",
            createdAt = "2024-01-01",
            width = 1920,
            height = 1080,
            color = "#FFFFFF",
            description = "A beautiful scenery",
            urls = PhotoUrls("raw", "full", "regular", "small", "thumb"),
            user = User("user_1", "photographer", "John Doe")
        )

        // ViewModel이 가짜 데이터를 리턴하도록 설정
        every { mockViewModel.photoDetail } returns MutableStateFlow(fakePhoto)
        every { mockViewModel.isBookmarked(any()) } returns flowOf(true) // 북마크 된 상태

        // When (화면 그리기 - Mock ViewModel 주입)
        composeRule.setContent {
            DetailScreen(
                photoId = "photo_1",
                onBackClick = {},
                viewModel = mockViewModel
            )
        }

        // Then (화면 검증)
        // 1. 설명 텍스트가 보이는지
        composeRule.onNodeWithText("ID: photo_1").assertIsDisplayed()
        composeRule.onNodeWithText("Author: John Doe").assertIsDisplayed()
        composeRule.onNodeWithText("Size: 1920 x 1080").assertIsDisplayed()

        // 2. 북마크 아이콘이 '채워진' 상태인지 확인 (ContentDescription 활용)
        // DetailScreen 코드에 따르면 북마크 상태일 때 아이콘 설명은 "Bookmark"
        composeRule.onNodeWithContentDescription("Bookmark").assertIsDisplayed()
    }
}