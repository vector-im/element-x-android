/*
 * Copyright (c) 2022 New Vector Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

@file:OptIn(
    ExperimentalAnimationApi::class,
    ExperimentalMaterialNavigationApi::class
)

package io.element.android.x

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowCompat
import androidx.navigation.NavHostController
import com.airbnb.mvrx.compose.mavericksActivityViewModel
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.animations.defaults.RootNavGraphDefaultAnimations
import com.ramcosta.composedestinations.animations.rememberAnimatedNavHostEngine
import com.ramcosta.composedestinations.spec.Route
import io.element.android.x.core.compose.OnLifecycleEvent
import io.element.android.x.designsystem.ElementXTheme
import io.element.android.x.destinations.OnBoardingScreenNavigationDestination
import io.element.android.x.features.rageshake.bugreport.BugReportScreen
import io.element.android.x.features.rageshake.crash.ui.CrashDetectionScreen
import io.element.android.x.features.rageshake.detection.RageshakeDetectionScreen
import io.element.android.x.tests.uitests.ShowkaseButton
import io.element.android.x.tests.uitests.openShowkase
import kotlinx.coroutines.runBlocking
import timber.log.Timber

private const val transitionAnimationDuration = 500

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            ElementXTheme {
                MainScreen(viewModel = mavericksActivityViewModel())
            }
        }
    }

    @Composable
    private fun MainScreen(viewModel: MainViewModel) {
        val startRoute = runBlocking {
            if (!viewModel.isLoggedIn()) {
                OnBoardingScreenNavigationDestination
            } else {
                viewModel.restoreSession()
                NavGraphs.root.startRoute
            }
        }

        var isBugReportVisible by remember { mutableStateOf(false) }

        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
            MainContent(
                startRoute = startRoute
            )
            ShowkaseButton(
                onClick = { openShowkase(this@MainActivity) }
            )
            RageshakeDetectionScreen(
                onOpenBugReport = {
                    isBugReportVisible = true
                }
            )
            CrashDetectionScreen(
                onOpenBugReport = {
                    isBugReportVisible = true
                }
            )
            if (isBugReportVisible) {
                // TODO Improve the navigation, when pressing back here, it closes the app.
                BugReportScreen(
                    onDone = { isBugReportVisible = false }
                )
            }
        }
        OnLifecycleEvent { _, event ->
            Timber.v("OnLifecycleEvent: $event")
        }
    }

    @Composable
    private fun MainContent(startRoute: Route) {
        val engine = rememberAnimatedNavHostEngine(
            rootDefaultAnimations = RootNavGraphDefaultAnimations(
                enterTransition = {
                    slideIntoContainer(
                        AnimatedContentScope.SlideDirection.Left,
                        animationSpec = tween(transitionAnimationDuration)
                    )
                },
                exitTransition = {
                    slideOutOfContainer(
                        AnimatedContentScope.SlideDirection.Left,
                        animationSpec = tween(transitionAnimationDuration)
                    )
                },
                popEnterTransition = {
                    slideIntoContainer(
                        AnimatedContentScope.SlideDirection.Right,
                        animationSpec = tween(transitionAnimationDuration)
                    )
                },
                popExitTransition = {
                    slideOutOfContainer(
                        AnimatedContentScope.SlideDirection.Right,
                        animationSpec = tween(transitionAnimationDuration)
                    )
                }
            )
        )
        val navController = engine.rememberNavController()
        LogNavigation(navController)

        DestinationsNavHost(
            modifier = Modifier.background(MaterialTheme.colorScheme.background),
            engine = engine,
            navController = navController,
            navGraph = NavGraphs.root,
            startRoute = startRoute,
            dependenciesContainerBuilder = {
            }
        )
    }

    @Composable
    private fun LogNavigation(navController: NavHostController) {
        LaunchedEffect(key1 = navController) {
            navController.appCurrentDestinationFlow.collect {
                Timber.d("Navigating to ${it.route}")
            }
        }
    }

    @Composable
    @Preview
    fun MainContentPreview() {
        MainContent(startRoute = OnBoardingScreenNavigationDestination)
    }
}
