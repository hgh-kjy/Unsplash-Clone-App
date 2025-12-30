package com.test.unsplashcloneapp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface BookmarkDao {
    @Query("SELECT * FROM bookmarks ORDER BY createdAt DESC")
    fun getAllBookmarks(): Flow<List<BookmarkEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookmark(bookmark: BookmarkEntity)

    @Query("DELETE FROM bookmarks WHERE id = :id")
    suspend fun deleteBookmark(id: String)

    // 특정 ID가 북마크 되어있는지 확인 (Flow로 실시간 관찰)
    @Query("SELECT EXISTS(SELECT 1 FROM bookmarks WHERE id = :id)")
    fun isBookmarked(id: String): Flow<Boolean>
}