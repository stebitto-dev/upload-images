package com.stebitto.uploadimages.ui.screens

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.stebitto.uploadimages.R
import com.stebitto.uploadimages.actions.SelectedCountry
import com.stebitto.uploadimages.states.CountryState
import com.stebitto.uploadimages.states.UploadImagesState
import com.stebitto.uploadimages.ui.MainViewModel

/**
 * enum values that represent the screens in the app
 */
enum class UploadImagesScreen(@StringRes val title: Int) {
    Countries(title = R.string.title_screen_countries),
    UploadImages(title = R.string.title_screen_upload_images)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(currentScreen: UploadImagesScreen, modifier: Modifier = Modifier) {
    TopAppBar(
        title = { Text(stringResource(currentScreen.title)) },
        modifier = modifier,
        colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UploadImagesApp(
    viewModel: MainViewModel = viewModel(),
    navHostController: NavHostController = rememberNavController()
) {
    // Get current back stack entry
    val backStackEntry by navHostController.currentBackStackEntryAsState()
    // Get the name of the current screen
    val currentScreen = UploadImagesScreen.valueOf(
        backStackEntry?.destination?.route ?: UploadImagesScreen.Countries.name
    )

    Scaffold(
        topBar = { TopBar(currentScreen = currentScreen) }
    ) { contentPadding ->
        val uiState = viewModel.state.collectAsStateWithLifecycle()

        NavHost(
            navController = navHostController,
            startDestination = UploadImagesScreen.Countries.name,
            modifier = Modifier.padding(contentPadding)
        ) {
            composable(route = UploadImagesScreen.Countries.name) {
                if (uiState.value is CountryState) {
                    CountryScreen(
                        uiState = uiState.value as CountryState,
                        onCountrySelect = {
                            viewModel.dispatch(SelectedCountry(it))
                            navHostController.navigate(UploadImagesScreen.UploadImages.name)
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
            composable(route = UploadImagesScreen.UploadImages.name) {
                if (uiState.value is UploadImagesState) {
                    UploadImagesScreen(
                        uiState = uiState.value as UploadImagesState,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}