package com.stebitto.uploadimages.ui.screens

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.stebitto.uploadimages.GOOGLE_PHOTO_PACKAGE_NAME
import com.stebitto.uploadimages.PICK_IMAGES_MAX_NUMBER
import com.stebitto.uploadimages.R
import com.stebitto.uploadimages.actions.PickedImages
import com.stebitto.uploadimages.actions.RetryLoadingCountries
import com.stebitto.uploadimages.actions.SelectedCountry
import com.stebitto.uploadimages.actions.UploadImages
import com.stebitto.uploadimages.copyTextToClipboard
import com.stebitto.uploadimages.getTmpFileUri
import com.stebitto.uploadimages.states.CountryState
import com.stebitto.uploadimages.states.UploadImagesState
import com.stebitto.uploadimages.ui.MainViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.UUID

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
    val context = LocalContext.current

    // Get current screen
    val backStackEntry by navHostController.currentBackStackEntryAsState()
    val currentScreen = UploadImagesScreen.valueOf(
        backStackEntry?.destination?.route ?: UploadImagesScreen.Countries.name
    )

    // Launch photo picker in multi-select mode
    val launcherGallery = rememberLauncherForActivityResult(
        ActivityResultContracts.PickMultipleVisualMedia(PICK_IMAGES_MAX_NUMBER)
    ) { uris ->
        if (uris.isNotEmpty()) {
            Timber.d("Number of items selected: ${uris.size}")
            viewModel.dispatch(PickedImages(uris))
        } else {
            Timber.d("No media selected")
        }
    }
    // Launch camera
    var cameraUri: Uri = Uri.EMPTY
    val launcherCamera =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { pictureWasTaken ->
            if (pictureWasTaken) {
                Timber.d("Picture saved at $cameraUri")
                viewModel.dispatch(PickedImages(listOf(cameraUri)))
            } else {
                Timber.d("No picture taken from camera")
            }
        }
    // Launch Google Photo
    val launcherGooglePhoto =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                Timber.d("Number of items selected: ${result.data?.clipData?.itemCount}")
                // get list of uris from result
                result.data?.clipData?.let {
                    val uriList = mutableListOf<Uri>()
                    for (i in 0..it.itemCount) {
                        uriList.add(it.getItemAt(i).uri)
                    }
                    viewModel.dispatch(PickedImages(uriList))
                }
            } else {
                Timber.d("No media selected")
            }
        }
    val googlePhotoIntent = Intent().apply {
        action = Intent.ACTION_PICK
        type = "image/*"
        `package` = GOOGLE_PHOTO_PACKAGE_NAME
        putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
    }

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = { TopBar(currentScreen = currentScreen) },
        bottomBar = {
            if (currentScreen == UploadImagesScreen.UploadImages) {
                UploadImagesBottomBar(
                    onGalleryClick = {
                        launcherGallery.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    onCameraClick = {
                        cameraUri = context.getTmpFileUri(UUID.randomUUID().toString())
                        launcherCamera.launch(cameraUri)
                    },
                    onGooglePhotoClick = {
                        try {
                            launcherGooglePhoto.launch(googlePhotoIntent)
                        } catch (e: ActivityNotFoundException) {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar(context.getString(R.string.error_google_photos))
                            }
                        }
                    },
                    onFABClick = { viewModel.dispatch(UploadImages) }
                )
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { contentPadding ->
        val uiState = viewModel.state.collectAsStateWithLifecycle()

        NavHost(
            navController = navHostController,
            startDestination = UploadImagesScreen.Countries.name,
            modifier = Modifier.padding(contentPadding)
        ) {
            composable(route = UploadImagesScreen.Countries.name) {
                if (uiState.value is CountryState) { // for extra security, compose only with proper state
                    CountryScreen(
                        uiState = uiState.value as CountryState,
                        modifier = Modifier.fillMaxSize(),
                        onCountrySelect = {
                            viewModel.dispatch(SelectedCountry(it))
                            navHostController.navigate(UploadImagesScreen.UploadImages.name) {
                                // remove Countries screen from back stack
                                popUpTo(UploadImagesScreen.Countries.name) { inclusive = true }
                            }
                        },
                        onRetry = {
                            viewModel.dispatch(RetryLoadingCountries)
                        }
                    )
                }
            }
            composable(route = UploadImagesScreen.UploadImages.name) {
                if (uiState.value is UploadImagesState) { // for extra security, compose only with proper state
                    UploadImagesScreen(
                        uiState = uiState.value as UploadImagesState,
                        modifier = Modifier.fillMaxSize(),
                        onUploadedImageClick = { uploadedImage ->
                            uploadedImage.url?.let { // if url is not populated, image is not uploaded yet
                                // copy url to clipboard
                                if (!context.copyTextToClipboard(it)) {
                                    // and inform user about it
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar(
                                            message = context.getString(R.string.text_copied_to_clipboard),
                                            duration = SnackbarDuration.Short
                                        )
                                    }
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}