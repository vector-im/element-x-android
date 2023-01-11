package io.element.android.x.features.preferences.root

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

@ContributesNode(SessionScope::class)
class PreferencesRootNode @AssistedInject constructor(
    @Assisted buildContext: BuildContext,
    @Assisted plugins: List<Plugin>,
    private val presenter: PreferencesRootPresenter,
) : Node(buildContext, plugins = plugins) {

    interface Callback : Plugin {
        fun onOpenBugReport()
    }

    private val presenterConnector = presenterConnector(presenter)

    private fun onOpenBugReport() {
        plugins<Callback>().forEach { it.onOpenBugReport() }
    }

    @Composable
    override fun View(modifier: Modifier) {
        val state by presenterConnector.stateFlow.collectAsState()
        PreferencesRootView(
            state = state,
            onBackPressed = this::navigateUp,
            onOpenRageShake = this::onOpenBugReport
        )
    }
}