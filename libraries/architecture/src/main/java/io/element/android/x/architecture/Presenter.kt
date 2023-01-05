package io.element.android.x.architecture

import androidx.compose.runtime.Composable
import kotlinx.coroutines.flow.Flow

interface Presenter<State, Event> {
    @Composable
    fun present(events: Flow<Event>): State
}