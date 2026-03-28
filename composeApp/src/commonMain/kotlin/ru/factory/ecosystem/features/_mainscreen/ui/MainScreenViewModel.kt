package ru.factory.ecosystem.features._mainscreen.ui

import ru.factory.ecosystem.core.ui.viewmodel.BaseViewModel

/**
 * Класс Вью-модель для навигации
 */

class MainScreenViewModel(

) : BaseViewModel<MainScreenState, MainScreenSideEffect>(MainScreenState()) {

    fun navigateTo() {
//        setState {
//            it.copy(globalState = GlobalState.CraftState())
//        }
    }

}