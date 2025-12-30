package com.test.unsplashcloneapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.test.unsplashcloneapp.presentation.ui.BookmarkScreen
import com.test.unsplashcloneapp.presentation.ui.DetailScreen
import com.test.unsplashcloneapp.presentation.ui.SearchScreen
import com.test.unsplashcloneapp.ui.theme.UnsplashCloneAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint // <--- 이 어노테이션이 없으면 에러가 발생합니다!
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            UnsplashCloneAppTheme {
                val navController = rememberNavController()

                NavHost(navController = navController, startDestination = "search") {

                    // 검색 화면
                    composable("search") {
                        SearchScreen(
                            onImageClick = { photoId ->
                                navController.navigate("detail/$photoId")
                            },
                            onBookmarkClick = {
                                navController.navigate("bookmark")
                            }
                        )
                    }

                    // 상세 화면
                    composable("detail/{photoId}") { backStackEntry ->
                        val photoId = backStackEntry.arguments?.getString("photoId")
                        DetailScreen(
                            photoId = photoId,
                            onBackClick = { navController.popBackStack() }
                        )
                    }

                    // 북마크 화면
                    composable("bookmark") {
                        BookmarkScreen(
                            onBackClick = { navController.popBackStack() },
                            onImageClick = { photoId ->
                                navController.navigate("detail/$photoId")
                            }
                        )
                    }
                }
            }
        }
    }
}