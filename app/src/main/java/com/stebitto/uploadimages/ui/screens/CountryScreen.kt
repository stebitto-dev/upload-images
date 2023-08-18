package com.stebitto.uploadimages.ui.screens

import android.content.res.Configuration
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.stebitto.uploadimages.datamodels.domain.Country
import com.stebitto.uploadimages.states.CountryState
import com.stebitto.uploadimages.ui.theme.UploadImagesTheme

@Composable
fun CountryScreen(uiState: CountryState, onCountrySelect: (Country) -> Unit, modifier: Modifier) {
    when (uiState) {
        is CountryState.Loading -> {}
        is CountryState.CountryList -> CountryList(
            countries = uiState.countries,
            modifier = modifier,
            onCountrySelect = { onCountrySelect(it) }
        )

        is CountryState.Error -> {}
    }
}

@Composable
fun CountryList(
    countries: List<Country>,
    modifier: Modifier = Modifier,
    onCountrySelect: (Country) -> Unit = {}
) {
    Surface(modifier = modifier) {
        LazyColumn(
            contentPadding = PaddingValues(vertical = 10.dp)
        ) {
            itemsIndexed(items = countries) { index, country ->
                CountryCard(country = country, onClick = onCountrySelect)

                if (index != countries.size - 1) {
                    Divider(thickness = 1.dp, color = MaterialTheme.colorScheme.tertiary)
                }
            }
        }
    }
}

@Composable
fun CountryCard(country: Country, onClick: (Country) -> Unit) {
    TextButton(
        onClick = { onClick(country) },
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = country.name,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 10.dp),
            style = MaterialTheme.typography.labelLarge,
            textAlign = TextAlign.Center
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
fun CountryListPreview() {
    UploadImagesTheme {
        CountryList(countries = List(20) { Country("Country $it") })
    }
}