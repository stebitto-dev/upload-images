package com.stebitto.uploadimages.ui.screens

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.stebitto.uploadimages.R
import com.stebitto.uploadimages.datamodels.domain.UploadedImage
import com.stebitto.uploadimages.states.UploadImagesState
import com.stebitto.uploadimages.ui.theme.UploadImagesTheme

@Composable
fun UploadImagesFAB(onClick: () -> Unit) {
    FloatingActionButton(
        onClick = { onClick() }
    ) {
        Icon(
            painter = painterResource(R.drawable.icon_cloud_upload),
            contentDescription = stringResource(R.string.bottom_bar_upload_description)
        )
    }
}

@Composable
fun UploadImagesBottomBar(
    onGalleryClick: () -> Unit = {},
    onCameraClick: () -> Unit = {},
    onGooglePhotoClick: () -> Unit = {},
    onFABClick: () -> Unit = {}
) {
    BottomAppBar(
        actions = {
            // Gallery
            IconButton(onClick = { onGalleryClick() }) {
                Icon(
                    painter = painterResource(R.drawable.icon_gallery),
                    contentDescription = stringResource(R.string.bottom_bar_gallery_description)
                )
            }
            // Camera
            IconButton(onClick = { onCameraClick() }) {
                Icon(
                    painter = painterResource(R.drawable.icon_photo_camera),
                    contentDescription = stringResource(R.string.bottom_bar_camera_description)
                )
            }
            // Google Photo
            IconButton(onClick = { onGooglePhotoClick() }) {
                Icon(
                    painter = painterResource(R.drawable.icon_google_logo),
                    contentDescription = stringResource(R.string.bottom_bar_google_photo_description)
                )
            }
        },
        floatingActionButton = { UploadImagesFAB { onFABClick() } }
    )
}

@Composable
fun UploadImagesScreen(
    uiState: UploadImagesState,
    modifier: Modifier,
    onUploadedImageClick: (UploadedImage) -> Unit
) {
    when (uiState) {
        is UploadImagesState.PickImages -> {
            EmptyListLabel(modifier)
        }

        is UploadImagesState.UploadedImages -> {
            UploadImagesList(
                images = uiState.images,
                modifier = modifier,
                onItemClick = onUploadedImageClick
            )
        }

        is UploadImagesState.Error -> {}
    }
}

@Composable
fun EmptyListLabel(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(R.string.upload_images_empty_list),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun UploadImagesList(
    images: List<UploadedImage>,
    modifier: Modifier = Modifier,
    onItemClick: (UploadedImage) -> Unit = {}
) {
    Surface(modifier = modifier) {
        LazyColumn {
            items(items = images) { image: UploadedImage ->
                UploadedImageCard(uploadedImage = image, onCardClick = onItemClick)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UploadedImageCard(
    uploadedImage: UploadedImage,
    onCardClick: (UploadedImage) -> Unit = {}
) {
    Card(onClick = { onCardClick(uploadedImage)} ) {
        Row(
            modifier = Modifier
                .padding(all = 8.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = uploadedImage.contentUri,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(text = uploadedImage.name)
            }

            Spacer(modifier = Modifier.width(8.dp))

            if (uploadedImage.isLoading) {
                CircularProgressIndicator()
            } else if (uploadedImage.isUploaded) {
                Icon(
                    painter = painterResource(id = R.drawable.icon_content_copy),
                    contentDescription = stringResource(id = R.string.upload_images_copy_text_description)
                )
            } else {
                Icon(
                    painter = painterResource(id = R.drawable.icon_cloud_off),
                    contentDescription = null
                )
            }
        }
    }
}

@Preview(
    name = "Light Mode",
    showBackground = true,
    widthDp = 320
)
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    widthDp = 320,
    showBackground = true,
    name = "Dark Mode"
)
@Composable
fun UploadImagesBottomBarPreview() {
    UploadImagesTheme {
        UploadImagesBottomBar()
    }
}

@Preview(
    name = "Light Mode",
    showBackground = true,
    widthDp = 320
)
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    widthDp = 320,
    showBackground = true,
    name = "Dark Mode"
)
@Composable
fun UploadedImageCardPreview() {
    UploadImagesTheme {
        UploadedImageCard(
            UploadedImage(
                id = "",
                contentUri = "",
                name = "Photo name test",
                isLoading = false,
                isUploaded = true
            )
        )
    }
}