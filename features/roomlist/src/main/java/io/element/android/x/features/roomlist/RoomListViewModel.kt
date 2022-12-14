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

package io.element.android.x.features.roomlist

import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.MavericksViewModel
import com.airbnb.mvrx.MavericksViewModelFactory
import com.airbnb.mvrx.Success
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import io.element.android.x.anvilannotations.ContributesViewModel
import io.element.android.x.core.coroutine.parallelMap
import io.element.android.x.core.di.daggerMavericksViewModelFactory
import io.element.android.x.designsystem.components.avatar.AvatarSize
import io.element.android.x.di.SessionScope
import io.element.android.x.features.roomlist.model.RoomListRoomSummary
import io.element.android.x.features.roomlist.model.RoomListRoomSummaryPlaceholders
import io.element.android.x.features.roomlist.model.RoomListViewState
import io.element.android.x.matrix.MatrixClient
import io.element.android.x.matrix.room.RoomSummary
import io.element.android.x.matrix.ui.MatrixItemHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

private const val extendedRangeSize = 40

@ContributesViewModel(SessionScope::class)
class RoomListViewModel @AssistedInject constructor(
    private val client: MatrixClient,
    @Assisted initialState: RoomListViewState
) : MavericksViewModel<RoomListViewState>(initialState) {

    companion object : MavericksViewModelFactory<RoomListViewModel, RoomListViewState> by daggerMavericksViewModelFactory()

    private val lastMessageFormatter = LastMessageFormatter()
    private val matrixUserHelper = MatrixItemHelper(client)

    init {
        handleInit()
    }

    fun filterRoom(filter: String) {
        setState {
            copy(
                filter = filter
            )
        }
    }

    fun updateVisibleRange(range: IntRange) {
        viewModelScope.launch {
            if (range.isEmpty()) return@launch
            val midExtendedRangeSize = extendedRangeSize / 2
            val extendedRangeStart = (range.first - midExtendedRangeSize).coerceAtLeast(0)
            // Safe to give bigger size than room list
            val extendedRangeEnd = range.last + midExtendedRangeSize
            val extendedRange = IntRange(extendedRangeStart, extendedRangeEnd)
            client.roomSummaryDataSource().setSlidingSyncRange(extendedRange)
        }
    }

    private fun handleInit() {
        // Observe the room list and the filter
        combine(
            client.roomSummaryDataSource().roomSummaries()
                .map(::mapRoomSummaries)
                .flowOn(Dispatchers.Default),
            stateFlow
                .map { it.filter }
                .distinctUntilChanged(),
        ) { list, filter ->
            if (filter.isEmpty()) {
                list
            } else {
                list.filter { it.name.contains(filter, ignoreCase = true) }
            }
        }
            .execute {
                copy(
                    rooms = when {
                        it is Loading ||
                            // Note: this second case will prevent to handle correctly the empty case
                            (it is Success && it().isEmpty() && filter.isEmpty()) -> {
                            // Show fake placeholders to avoid having empty screen
                            Loading(RoomListRoomSummaryPlaceholders.createFakeList(size = 16))
                        }
                        else -> {
                            it
                        }
                    }
                )
            }
    }

    private suspend fun mapRoomSummaries(
        roomSummaries: List<RoomSummary>
    ): List<RoomListRoomSummary> {
        return roomSummaries.parallelMap { roomSummary ->
            when (roomSummary) {
                is RoomSummary.Empty -> RoomListRoomSummaryPlaceholders.create(roomSummary.identifier)
                is RoomSummary.Filled -> {
                    val avatarData = matrixUserHelper.loadAvatarData(
                        roomSummary = roomSummary,
                        size = AvatarSize.MEDIUM
                    )
                    RoomListRoomSummary(
                        id = roomSummary.identifier(),
                        name = roomSummary.details.name,
                        hasUnread = roomSummary.details.unreadNotificationCount > 0,
                        timestamp = lastMessageFormatter.format(roomSummary.details.lastMessageTimestamp),
                        lastMessage = roomSummary.details.lastMessage,
                        avatarData = avatarData,
                    )
                }
            }
        }
    }
}
