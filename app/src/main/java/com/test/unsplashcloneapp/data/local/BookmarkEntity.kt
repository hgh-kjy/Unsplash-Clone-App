package com.test.unsplashcloneapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bookmarks")
data class BookmarkEntity(
    @PrimaryKey val id: String,
    val imageUrl: String,
    val author: String,
    val width: Int,
    val height: Int,
    val createdAt: String
)