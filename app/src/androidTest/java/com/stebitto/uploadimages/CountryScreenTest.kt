package com.stebitto.uploadimages

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onChildren
import androidx.compose.ui.test.onNodeWithTag
import com.stebitto.uploadimages.datamodels.domain.Country
import com.stebitto.uploadimages.states.CountryState
import com.stebitto.uploadimages.ui.screens.CountryScreen
import com.stebitto.uploadimages.ui.screens.TEST_TAG_COUNTRY_LIST
import com.stebitto.uploadimages.ui.screens.TEST_TAG_ERROR_BUTTON
import com.stebitto.uploadimages.ui.screens.TEST_TAG_LOADER
import com.stebitto.uploadimages.ui.theme.UploadImagesTheme
import org.junit.Rule
import org.junit.Test

class CountryScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun showLoading() {
        composeTestRule.setContent {
            UploadImagesTheme {
                CountryScreen(uiState = CountryState.Loading)
            }
        }

        composeTestRule.onNodeWithTag(TEST_TAG_LOADER).assertExists()
    }

    @Test
    fun showError() {
        composeTestRule.setContent {
            UploadImagesTheme {
                CountryScreen(uiState = CountryState.Error())
            }
        }

        composeTestRule.onNodeWithTag(TEST_TAG_ERROR_BUTTON).assertExists()
    }

    @Test
    fun showCountryList() {
        val countryList = List(5) { Country("Country $it") }
        composeTestRule.setContent {
            UploadImagesTheme {
                CountryScreen(uiState = CountryState.CountryList(countryList))
            }
        }

        composeTestRule.onNodeWithTag(TEST_TAG_COUNTRY_LIST).onChildren().assertCountEquals(5)
    }
}