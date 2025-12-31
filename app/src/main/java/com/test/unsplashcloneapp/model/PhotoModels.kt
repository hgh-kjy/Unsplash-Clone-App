package com.test.unsplashcloneapp.model

import com.google.gson.annotations.SerializedName

data class SearchResponse(
    @SerializedName("results") val results: List<UnsplashPhoto>,
    @SerializedName("total_pages") val totalPages: Int
)

data class UnsplashPhoto(
    val id: String,
    @SerializedName("created_at") val createdAt: String,
    val width: Int,
    val height: Int,
    val color: String? = "#E0E0E0",
    val description: String?,
    val urls: PhotoUrls,
    val user: User
)

data class PhotoUrls(
    val raw: String,
    val full: String,
    val regular: String,
    val small: String,
    val thumb: String
)

data class User(
    val id: String,
    val username: String,
    val name: String
)