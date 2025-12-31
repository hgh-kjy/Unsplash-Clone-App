package com.test.unsplashcloneapp.presentation.ui

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import com.test.unsplashcloneapp.R
import com.test.unsplashcloneapp.model.PhotoUrls
import com.test.unsplashcloneapp.model.UnsplashPhoto
import com.test.unsplashcloneapp.model.User
import com.test.unsplashcloneapp.presentation.DetailViewModel
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class DetailScreenTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    private val mockViewModel = mockk<DetailViewModel>(relaxed = true)

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun detail_screen_displays_photo_information_using_resources() {
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

        every { mockViewModel.photoDetail } returns MutableStateFlow(fakePhoto)
        every { mockViewModel.isBookmarked(any()) } returns flowOf(true)

        composeRule.setContent {
            DetailScreen(
                photoId = "photo_1",
                onBackClick = {},
                viewModel = mockViewModel
            )
        }

        val context = composeRule.activity

        val title = context.getString(R.string.detail_title)
        composeRule.onNodeWithText(title).assertIsDisplayed()

        val labelId = context.getString(R.string.label_id)
        composeRule.onNodeWithText(labelId).assertIsDisplayed()
        composeRule.onNodeWithText("photo_1").assertIsDisplayed()

        val labelAuthor = context.getString(R.string.label_author)
        composeRule.onNodeWithText(labelAuthor).assertIsDisplayed()
        composeRule.onNodeWithText("John Doe").assertIsDisplayed()

        val labelSize = context.getString(R.string.label_size)
        composeRule.onNodeWithText(labelSize).assertIsDisplayed()
        composeRule.onNodeWithText("1920 x 1080").assertIsDisplayed()

        val descBookmark = context.getString(R.string.desc_bookmark)
        composeRule.onNodeWithContentDescription(descBookmark).assertIsDisplayed()
    }
}