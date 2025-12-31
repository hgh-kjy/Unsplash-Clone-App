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
        val bookmark = BookmarkEntity(
            id = "photo1",
            imageUrl = "url",
            author = "author",
            width = 100,
            height = 100,
            createdAt = "2024-01-01"
        )

        dao.insertBookmark(bookmark)
        val list = dao.getAllBookmarks().first()

        assertEquals(1, list.size)
        assertEquals(bookmark.id, list[0].id)
    }

    @Test
    fun delete_bookmark() = runTest {
        val bookmark = BookmarkEntity("photo1", "url", "author", 100, 100, "date")
        dao.insertBookmark(bookmark)

        dao.deleteBookmark("photo1")
        val list = dao.getAllBookmarks().first()

        assertTrue(list.isEmpty())
    }

    @Test
    fun insert_and_delete_multiple_bookmarks() = runTest {
        val list = listOf(
            BookmarkEntity("1", "url1", "a", 100, 100, "d1"),
            BookmarkEntity("2", "url2", "b", 100, 100, "d2"),
            BookmarkEntity("3", "url3", "c", 100, 100, "d3")
        )
        dao.insertBookmarks(list)

        val idsToDelete = listOf("1", "3")
        dao.deleteBookmarks(idsToDelete)

        val stored = dao.getAllBookmarks().first()
        assertEquals(1, stored.size)
        assertEquals("2", stored[0].id)
    }
}
