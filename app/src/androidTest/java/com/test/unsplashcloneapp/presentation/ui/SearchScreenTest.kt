package com.test.unsplashcloneapp.presentation.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.test.unsplashcloneapp.MainActivity
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class SearchScreenTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun search_screen_elements_are_displayed_correctly() {
        // 앱바 타이틀 확인
        composeRule.onNodeWithText("Unsplash Clone").assertIsDisplayed()

        // 검색창 플레이스홀더 확인
        composeRule.onNodeWithText("Search photos...").assertIsDisplayed()

        // 북마크 아이콘 확인
        composeRule.onNodeWithContentDescription("Bookmarks").assertIsDisplayed()
    }

    @Test
    fun can_input_search_query_and_click_search_button() {
        // 검색창에 "Korea" 입력
        composeRule.onNodeWithText("Search photos...")
            .performTextInput("Korea")

        // 검색 아이콘 클릭
        composeRule.onNodeWithContentDescription("Search")
            .performClick()
    }
}