package io.element.android.x.features.roomlist

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.plugin.Plugin
import com.bumble.appyx.core.plugin.plugins
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import io.element.android.x.anvilannotations.ContributesNode
import io.element.android.x.architecture.presenterConnector
import io.element.android.x.di.SessionScope
import io.element.android.x.features.roomlist.model.RoomListEvents
import io.element.android.x.matrix.core.RoomId

@ContributesNode(SessionScope::class)
class RoomListNode @AssistedInject constructor(
    @Assisted buildContext: BuildContext,
    @Assisted plugins: List<Plugin>,
    presenter: RoomListPresenter,
) : Node(buildContext, plugins = plugins) {

    interface Callback : Plugin {
        fun onRoomClicked(roomId: RoomId)
        fun onSettingsClicked()
    }

    private val connector = presenterConnector(presenter)

    private fun updateFilter(filter: String) {
        connector.emitEvent(RoomListEvents.UpdateFilter(filter))
    }

    private fun updateVisibleRange(range: IntRange) {
        connector.emitEvent((RoomListEvents.UpdateVisibleRange(range)))
    }

    private fun logout() {
        connector.emitEvent(RoomListEvents.Logout)
    }

    private fun onRoomClicked(roomId: RoomId) {
        plugins<Callback>().forEach { it.onRoomClicked(roomId) }
    }

    private fun onOpenSettings() {
        plugins<Callback>().forEach { it.onSettingsClicked() }
    }

    @Composable
    override fun View(modifier: Modifier) {
        val state by connector.stateFlow.collectAsState()
        RoomListView(
            state = state,
            onRoomClicked = this::onRoomClicked,
            onFilterChanged = this::updateFilter,
            onScrollOver = this::updateVisibleRange,
            onOpenSettings = this::onOpenSettings
        )
    }
}