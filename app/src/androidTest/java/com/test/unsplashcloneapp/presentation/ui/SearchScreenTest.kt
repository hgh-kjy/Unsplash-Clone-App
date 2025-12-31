package com.test.unsplashcloneapp.presentation.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.test.unsplashcloneapp.MainActivity
import com.test.unsplashcloneapp.R
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
        val context = composeRule.activity

        val title = context.getString(R.string.search_title)
        composeRule.onNodeWithText(title).assertIsDisplayed()

        val placeholder = context.getString(R.string.search_placeholder)
        composeRule.onNodeWithText(placeholder).assertIsDisplayed()

        val bookmarkDesc = context.getString(R.string.desc_bookmark)
        composeRule.onNodeWithContentDescription(bookmarkDesc).assertIsDisplayed()
    }

    @Test
    fun can_input_search_query_and_click_search_button() {
        val context = composeRule.activity
        val placeholder = context.getString(R.string.search_placeholder)
        composeRule.onNodeWithText(placeholder)
            .performTextInput("Korea")

        val bookmarkDesc = context.getString(R.string.desc_bookmark)
        composeRule.onNodeWithContentDescription(bookmarkDesc)
            .performClick()
    }
}