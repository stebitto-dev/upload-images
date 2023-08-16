package com.stebitto.uploadimages.ui

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.stebitto.uploadimages.R
import com.stebitto.uploadimages.actions.SelectedCountry
import com.stebitto.uploadimages.datamodels.domain.Country
import com.stebitto.uploadimages.states.CountryState
import com.stebitto.uploadimages.ui.theme.UploadImagesTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CountryActivity : ComponentActivity() {

    //TODO button ripple effect

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            UploadImagesTheme {
                Scaffold(
                    topBar = { TopBar(title = getString(R.string.title_activity_countries)) }
                ) { contentPadding ->
                    CountryScreen(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(contentPadding)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(title: String) {
    TopAppBar(
        title = { Text(text = title) },
        colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
    )
}

@Composable
fun CountryScreen(modifier: Modifier = Modifier, countryViewModel: CountryViewModel = viewModel()) {

    val uiState =
        countryViewModel.state.collectAsStateWithLifecycle(initialValue = CountryState.Loading)

    when (uiState.value) {
        is CountryState.Loading -> {}
        is CountryState.CountryList -> CountryList(
            countries = (uiState.value as CountryState.CountryList).countries,
            modifier = modifier,
            onCountrySelect = { countryViewModel.dispatch(SelectedCountry(it)) }
        )
        is CountryState.Error -> {}
        is CountryState.SelectCountry -> {}
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
                CountryCard(country = country, onCountrySelect)

                if (index != countries.size-1) {
                    Divider(thickness = 1.dp, color = MaterialTheme.colorScheme.tertiary)
                }
            }
        }
    }
}

@Composable
fun CountryCard(country: Country, onClick:(Country) -> Unit) {
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
fun TopBarPreview() {
    UploadImagesTheme {
        TopBar(title = "Title")
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