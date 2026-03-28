package ru.factory.ecosystem.core.ui.viewmodel


import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

abstract class BaseViewModel<State : UiState, Effect : UiSideEffect>(
    initialState: State,
) {

    private val _state: MutableStateFlow<State> = MutableStateFlow(initialState)
    val state = _state.asStateFlow()

    private val _sideEffect: MutableSharedFlow<Effect> = MutableSharedFlow(
        replay = 0,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    // Вместо Channel, не сохраняем в буфере, храним один элемент, удаляем старый при
    // переполнении.
    val sideEffect = _sideEffect.asSharedFlow()

    protected fun setState(transform: (State) -> State) {
        _state.update(transform)
    }
    // state.update {} уже потокобезопасный, это не suspend

    protected fun tryPostSideEffect(builder: () -> Effect) {
        _sideEffect.tryEmit(builder())
    }

    protected suspend fun postSideEffect(effect: Effect) {
        _sideEffect.emit(effect)
    }

}