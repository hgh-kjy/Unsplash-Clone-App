package com.test.unsplashcloneapp.di

import android.content.Context
import androidx.room.Room
import com.test.unsplashcloneapp.data.local.AppDatabase
import com.test.unsplashcloneapp.data.local.BookmarkDao
import com.test.unsplashcloneapp.data.remote.UnsplashService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideUnsplashService(): UnsplashService {
        return Retrofit.Builder()
            .baseUrl("https://api.unsplash.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(UnsplashService::class.java)
    }

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "unsplash_clone.db"
        ).build()
    }

    @Provides
    fun provideBookmarkDao(database: AppDatabase): BookmarkDao {
        return database.bookmarkDao()
    }
}