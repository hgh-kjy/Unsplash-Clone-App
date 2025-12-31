package com.test.unsplashcloneapp.data.local

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BookmarkDaoTest {

    private lateinit var db: AppDatabase
    private lateinit var dao: BookmarkDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        // 테스트용으로는 메모리 DB를 사용하여 종료 시 데이터가 휘발되도록 함
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .build()
        dao = db.bookmarkDao()
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun insert_and_read_bookmark() = runTest {
        // Given
        val bookmark = BookmarkEntity(
            id = "photo1",
            imageUrl = "url",
            author = "author",
            width = 100,
            height = 100,
            createdAt = "2024-01-01"
        )

        // When
        dao.insertBookmark(bookmark)
        val list = dao.getAllBookmarks().first() // Flow의 첫 번째 값을 가져옴

        // Then
        assertEquals(1, list.size)
        assertEquals(bookmark.id, list[0].id)
    }

    @Test
    fun delete_bookmark() = runTest {
        // Given
        val bookmark = BookmarkEntity("photo1", "url", "author", 100, 100, "date")
        dao.insertBookmark(bookmark)

        // When
        dao.deleteBookmark("photo1")
        val list = dao.getAllBookmarks().first()

        // Then
        assertTrue(list.isEmpty())
    }
}