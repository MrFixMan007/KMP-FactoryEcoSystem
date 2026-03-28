package ru.factory.ecosystem.features.entryscreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun EntryScreenContent(
    label: String,
    onMainButonClick: () -> Unit,
) {

    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.primaryContainer)
            .safeContentPadding()
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Button(
            onClick = { onMainButonClick() }
        ) {
            Text(label)
        }
    }

}

@Composable
@Preview
private fun Preview() {
    MaterialTheme {
        EntryScreenContent(
            label = "label",
            onMainButonClick = {},
        )
    }
}