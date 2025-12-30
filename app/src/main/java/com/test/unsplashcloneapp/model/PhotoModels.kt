package com.test.unsplashcloneapp.model

import com.google.gson.annotations.SerializedName

// API 응답: 검색 결과 래퍼
data class SearchResponse(
    @SerializedName("results") val results: List<UnsplashPhoto>,
    @SerializedName("total_pages") val totalPages: Int
)

// API 응답: 개별 사진 정보
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