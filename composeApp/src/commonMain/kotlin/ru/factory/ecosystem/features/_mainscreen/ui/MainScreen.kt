package ru.factory.ecosystem.features._mainscreen.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.collectLatest
import ru.factory.ecosystem.core.di.InjectorClient
import ru.factory.ecosystem.core.ui.GlobalState
import ru.factory.ecosystem.features.alertscreen.AlertScreen
import ru.factory.ecosystem.features.entryscreen.EntryScreen

@Composable
fun MainScreen() {

    val viewModel = InjectorClient.inject<MainScreenViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    // Слушаем SideEffect для показа уведомления
    LaunchedEffect(viewModel) {
        viewModel.sideEffect.collectLatest { effect ->
            when (effect) {
                is MainScreenSideEffect.ShowNotification -> {
                    snackbarHostState.showSnackbar(effect.message)
                }
            }
        }
    }

    Scaffold(
        snackbarHost = {
            // Хост для всплывающих уведомлений
            SnackbarHost(hostState = snackbarHostState)
        },
        floatingActionButton = {
            // Кнопка переподключения, если соединение потеряно
            if (!state.isServerConnected) {
                FloatingActionButton(
                    onClick = { viewModel.reconnect() },
                    containerColor = if (state.isConnecting) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.primaryContainer
                ) {
                    if (state.isConnecting) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Переподключиться"
                        )
                    }
                }
            }
        }
    ) { innersPadding ->
        Box(modifier = Modifier.padding(innersPadding)) {
            when (val globalState = state.globalState) {
                is GlobalState.EntryState -> {
                    EntryScreen()
                }

                is GlobalState.AlertState -> {
                    AlertScreen(
                        initState = globalState.screenState
                    )
                }
            }
        }
    }

}