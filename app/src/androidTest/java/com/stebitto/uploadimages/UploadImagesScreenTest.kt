package com.stebitto.uploadimages

import android.net.Uri
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import com.stebitto.uploadimages.datamodels.domain.AppImage
import com.stebitto.uploadimages.datamodels.domain.UploadImageStatus
import com.stebitto.uploadimages.states.UploadImagesState
import com.stebitto.uploadimages.ui.screens.TEST_TAG_EMPTY_LABEL
import com.stebitto.uploadimages.ui.screens.TEST_TAG_IMAGE_CARD
import com.stebitto.uploadimages.ui.screens.TEST_TAG_IMAGE_CARD_REMOVE_ACTION
import com.stebitto.uploadimages.ui.screens.UploadImagesScreen
import com.stebitto.uploadimages.ui.theme.UploadImagesTheme
import org.junit.Rule
import org.junit.Test

class UploadImagesScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun showEmptyLabel() {
        composeTestRule.setContent {
            UploadImagesTheme {
                UploadImagesScreen(uiState = UploadImagesState.PickImages())
            }
        }

        composeTestRule.onNodeWithTag(TEST_TAG_EMPTY_LABEL).assertExists()
    }

    @Test
    fun showImagesList() {
        val uploadedImages =
            List(4) { AppImage(contentUri = Uri.EMPTY, status = UploadImageStatus.TO_UPLOAD) }
        val imagesToUpload =
            List(2) { AppImage(contentUri = Uri.EMPTY, status = UploadImageStatus.TO_UPLOAD) }
        composeTestRule.setContent {
            UploadImagesTheme {
                UploadImagesScreen(
                    uiState = UploadImagesState.PickImages(
                        uploadedImages = uploadedImages,
                        imagesToUpload = imagesToUpload
                    )
                )
            }
        }

        composeTestRule.onAllNodesWithTag(TEST_TAG_IMAGE_CARD)
            .assertCountEquals(uploadedImages.size + imagesToUpload.size)
    }

    @Test
    fun showImageListWithoutRemoveActionDuringUpload() {
        val uploadingImages =
            List(4) { AppImage(contentUri = Uri.EMPTY, status = UploadImageStatus.TO_UPLOAD) }
        composeTestRule.setContent {
            UploadImagesTheme {
                UploadImagesScreen(
                    uiState = UploadImagesState.UploadingImages(
                        uploadingImages = uploadingImages
                    )
                )
            }
        }

        composeTestRule.onNodeWithTag(TEST_TAG_IMAGE_CARD_REMOVE_ACTION).assertDoesNotExist()
    }
}