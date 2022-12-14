/*
 * Copyright (c) 2022 New Vector Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.element.android.x.features.messages.textcomposer

import com.airbnb.mvrx.MavericksViewModel
import com.airbnb.mvrx.MavericksViewModelFactory
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import io.element.android.x.anvilannotations.ContributesViewModel
import io.element.android.x.core.data.StableCharSequence
import io.element.android.x.core.di.daggerMavericksViewModelFactory
import io.element.android.x.di.SessionScope
import io.element.android.x.matrix.MatrixClient

@ContributesViewModel(SessionScope::class)
class MessageComposerViewModel @AssistedInject constructor(
    private val client: MatrixClient,
    @Assisted private val initialState: MessageComposerViewState
) : MavericksViewModel<MessageComposerViewState>(initialState) {

    companion object :
        MavericksViewModelFactory<MessageComposerViewModel, MessageComposerViewState> by daggerMavericksViewModelFactory()

    fun onComposerFullScreenChange() {
        setState {
            copy(
                isFullScreen = !isFullScreen
            )
        }
    }

    fun updateText(newText: CharSequence) {
        setState {
            copy(
                text = StableCharSequence(newText),
                isSendButtonVisible = newText.isNotEmpty(),
            )
        }
    }
}
