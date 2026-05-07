package ru.factory.ecosystem.features.alertscreen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.collectLatest
import ru.factory.ecosystem.core.di.InjectorClient
import ru.factory.ecosystem.features._mainscreen.ui.MainScreenViewModel

@Composable
fun AlertScreen(initState: AlertScreenState) {
    val viewModel = InjectorClient.inject<AlertScreenViewModel>()
    viewModel.init(state = initState)
    val state by viewModel.state.collectAsStateWithLifecycle()

    val mainViewModel = InjectorClient.inject<MainScreenViewModel>()

    LaunchedEffect(viewModel) {
        viewModel.sideEffect.collectLatest { effect ->
            when (effect) {
                is AlertScreenSideEffect.NavigateTo -> {
                    mainViewModel.navigateTo(effect.newState)
                }
            }
        }
    }

    AlertScreenContent(
        title = state.title,
        descriptionText = state.descriptionText,
        bottomButonText = state.bottomButonText,
        onBottomButonClick = viewModel::onBottomButtonClicked,
    )
}