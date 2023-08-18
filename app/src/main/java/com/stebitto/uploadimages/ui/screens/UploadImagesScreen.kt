package com.stebitto.uploadimages.ui.screens

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.stebitto.uploadimages.R
import com.stebitto.uploadimages.states.UploadImagesState
import com.stebitto.uploadimages.ui.theme.UploadImagesTheme

@Composable
fun UploadImagesFAB(onClick: () -> Unit) {
    FloatingActionButton(
        onClick = { onClick() }
    ) {
        Icon(Icons.Filled.Add, "Upload images")
    }
}

@Composable
fun UploadImagesScreen(
    uiState: UploadImagesState,
    modifier: Modifier
) {
    when (uiState) {
        is UploadImagesState.PickImages -> { EmptyListLabel(modifier) }
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

@Preview(name = "Light Mode", widthDp = 320)
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    widthDp = 320,
    showBackground = true,
    name = "Dark Mode"
)
@Composable
fun Preview() {
    UploadImagesTheme {
        UploadImagesFAB {}
    }
}