package com.test.unsplashcloneapp.presentation.ui

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import com.test.unsplashcloneapp.R
import com.test.unsplashcloneapp.data.local.BookmarkEntity
import com.test.unsplashcloneapp.presentation.BookmarkViewModel
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class BookmarkScreenTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    private val mockViewModel = mockk<BookmarkViewModel>(relaxed = true)

    @Before
    fun setup() {
        hiltRule.inject()

        every { mockViewModel.selectedIds } returns MutableStateFlow(emptySet())
    }

    @Test
    fun bookmark_screen_displays_empty_message_using_resources() {
        every { mockViewModel.bookmarks } returns MutableStateFlow(emptyList())

        composeRule.setContent {
            BookmarkScreen(
                onBackClick = {},
                onImageClick = {},
                viewModel = mockViewModel
            )
        }

        val emptyMsg = composeRule.activity.getString(R.string.empty_bookmarks)
        composeRule.onNodeWithText(emptyMsg).assertIsDisplayed()
    }

    @Test
    fun bookmark_screen_displays_grid_items_using_resources() {
        val bookmarks = listOf(
            BookmarkEntity("1", "url1", "user1", 100, 100, "date"),
            BookmarkEntity("2", "url2", "user2", 100, 100, "date")
        )
        every { mockViewModel.bookmarks } returns MutableStateFlow(bookmarks)

        composeRule.setContent {
            BookmarkScreen(
                onBackClick = {},
                onImageClick = {},
                viewModel = mockViewModel
            )
        }

        val context = composeRule.activity

        val emptyMsg = context.getString(R.string.empty_bookmarks)
        composeRule.onNodeWithText(emptyMsg).assertDoesNotExist()

        val photoDesc = context.getString(R.string.desc_photo)
        val images = composeRule.onAllNodesWithContentDescription(photoDesc)

        assert(images.fetchSemanticsNodes().size == 2)
    }
}