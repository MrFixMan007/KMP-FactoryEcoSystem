package ru.factory.ecosystem.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun AlertView(
    title: String,
    descriptionText: String,
    bottomButonText: String,
    onBottomButonClick: () -> Unit,
    modifier: Modifier = Modifier,
) {

    Column(
        modifier = modifier
            .background(MaterialTheme.colorScheme.primaryContainer)
            .safeContentPadding()
            .padding(horizontal = 12.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Warning,
            tint = Color(0xFF890000),
            contentDescription = null,
            modifier = Modifier.size(180.dp)
        )
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = Color(0xFF890000)
        )
        Text(
            text = descriptionText,
            style = MaterialTheme.typography.bodyLarge,
            color = Color(0xFF890000)
        )
        Button(
            onClick = { onBottomButonClick() },
            modifier = Modifier
                .height(36.dp)
                .fillMaxWidth()
        ) {
            Text(bottomButonText)
        }
    }

}

@Composable
@Preview
private fun Preview() {
    MaterialTheme {
        AlertView(
            bottomButonText = "Принято",
            title = "Внимание, тревога!",
            descriptionText = "Покиньте помещение!",
            onBottomButonClick = {},
            modifier = Modifier.fillMaxSize()
        )
    }
}