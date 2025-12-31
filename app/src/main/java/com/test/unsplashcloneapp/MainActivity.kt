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

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            UnsplashCloneAppTheme {
                val navController = rememberNavController()

                NavHost(navController = navController, startDestination = "search") {
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

                    composable("detail/{photoId}") { backStackEntry ->
                        val photoId = backStackEntry.arguments?.getString("photoId")
                        DetailScreen(
                            photoId = photoId,
                            onBackClick = {
                                if (navController.previousBackStackEntry != null) {
                                    navController.popBackStack()
                                }
                            }
                        )
                    }

                    composable("bookmark") {
                        BookmarkScreen(
                            onBackClick = {
                                if (navController.previousBackStackEntry != null) {
                                    navController.popBackStack()
                                }
                            },
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