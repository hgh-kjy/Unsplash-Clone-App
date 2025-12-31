package com.test.unsplashcloneapp.presentation

import com.test.unsplashcloneapp.data.local.BookmarkDao
import com.test.unsplashcloneapp.data.remote.UnsplashService
import com.test.unsplashcloneapp.model.PhotoUrls
import com.test.unsplashcloneapp.model.UnsplashPhoto
import com.test.unsplashcloneapp.model.User
import com.test.unsplashcloneapp.util.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class DetailViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: DetailViewModel
    private val api: UnsplashService = mockk()
    private val dao: BookmarkDao = mockk(relaxed = true)

    @Before
    fun setup() {
        viewModel = DetailViewModel(api, dao)
    }

    @Test
    fun loadPhotoDetail_fetches_data_successfully_from_api() = runTest {
        val fakePhoto = UnsplashPhoto(
            id = "photo1",
            createdAt = "2024-01-01",
            width = 100,
            height = 100,
            color = "#000000",
            description = "Test Description",
            urls = PhotoUrls("raw", "full", "regular", "small", "thumb"),
            user = User("user1", "username", "Test User")
        )
        coEvery { api.getPhotoDetail("photo1") } returns fakePhoto

        viewModel.loadPhotoDetail("photo1")

        val result = viewModel.photoDetail.value
        assertEquals("photo1", result?.id)
        assertEquals("Test User", result?.user?.name)

        coVerify(exactly = 1) { api.getPhotoDetail("photo1") }
    }

    @Test
    fun toggleBookmark_adds_bookmark_when_not_bookmarked() = runTest {
        val photo = UnsplashPhoto(
            id = "photo1",
            createdAt = "2024-01-01",
            width = 100,
            height = 100,
            color = null, description = null,
            urls = PhotoUrls("", "", "", "", ""),
            user = User("u1", "user", "name")
        )

        viewModel.toggleBookmark(photo, isCurrentlyBookmarked = false)

        coVerify(exactly = 1) { dao.insertBookmark(any()) }
    }

    @Test
    fun toggleBookmark_removes_bookmark_when_already_bookmarked() = runTest {
        val photo = UnsplashPhoto(
            id = "photo1",
            createdAt = "2024-01-01",
            width = 100,
            height = 100,
            color = null, description = null,
            urls = PhotoUrls("", "", "", "", ""),
            user = User("u1", "user", "name")
        )

        viewModel.toggleBookmark(photo, isCurrentlyBookmarked = true)

        coVerify(exactly = 1) { dao.deleteBookmark("photo1") }
    }
}