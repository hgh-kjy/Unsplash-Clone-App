package com.test.unsplashcloneapp.data.remote

import com.test.unsplashcloneapp.model.SearchResponse
import com.test.unsplashcloneapp.model.UnsplashPhoto
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface UnsplashService {

    @GET("search/photos")
    suspend fun searchPhotos(
        @Query("query") query: String,
        @Query("page") page: Int,
        @Query("per_page") perPage: Int = 30,
        @Header("Authorization") clientId: String = "EG5T4YGdmvax-jmGL84bc00xvEWhxNo-7MWTm6Td7I4"
    ): SearchResponse

    @GET("photos/{id}")
    suspend fun getPhotoDetail(
        @Path("id") id: String,
        @Header("Authorization") clientId: String = "EG5T4YGdmvax-jmGL84bc00xvEWhxNo-7MWTm6Td7I4"
    ): UnsplashPhoto
}