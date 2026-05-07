package ru.factory.ecosystem.features.alertscreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import ru.factory.ecosystem.core.ui.components.AlertView

@Composable
fun AlertScreenContent(
    title: String,
    descriptionText: String,
    bottomButonText: String,
    onBottomButonClick: () -> Unit,
) {

    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.primaryContainer)
            .safeContentPadding()
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        AlertView(
            title = title,
            descriptionText = descriptionText,
            bottomButonText = bottomButonText,
            onBottomButonClick = onBottomButonClick
        )
    }

}

@Composable
@Preview
private fun Preview() {
    MaterialTheme {
        AlertScreenContent(
            title = "Принято",
            descriptionText = "Внимание, тревога!",
            bottomButonText = "Покиньте помещение!",
            onBottomButonClick = {},
        )
    }
}