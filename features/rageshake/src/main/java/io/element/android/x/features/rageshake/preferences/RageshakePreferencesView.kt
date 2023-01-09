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

package io.element.android.x.features.rageshake.preferences

import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import io.element.android.x.designsystem.components.preferences.PreferenceCategory
import io.element.android.x.designsystem.components.preferences.PreferenceSlide
import io.element.android.x.designsystem.components.preferences.PreferenceSwitch
import io.element.android.x.designsystem.components.preferences.PreferenceText
import io.element.android.x.element.resources.R as ElementR

@Composable
fun RageshakePreferencesView(
    state: RageshakePreferencesState,
    modifier: Modifier = Modifier,
    onOpenRageshake: () -> Unit = {},
    onIsEnabledChanged: (Boolean) -> Unit = {},
    onSensitivityChanged: (Float) -> Unit = {}
) {
    Column(modifier = modifier) {
        PreferenceCategory(title = stringResource(id = ElementR.string.send_bug_report)) {
            PreferenceText(
                title = stringResource(id = ElementR.string.send_bug_report),
                icon = Icons.Default.BugReport,
                onClick = onOpenRageshake
            )
        }
        PreferenceCategory(title = stringResource(id = ElementR.string.settings_rageshake)) {
            if (state.isSupported) {
                PreferenceSwitch(
                    title = stringResource(id = ElementR.string.send_bug_report_rage_shake),
                    isChecked = state.isEnabled,
                    onCheckedChange = onIsEnabledChanged
                )
                PreferenceSlide(
                    title = stringResource(id = ElementR.string.settings_rageshake_detection_threshold),
                    // summary = stringResource(id = ElementR.string.settings_rageshake_detection_threshold_summary),
                    value = state.sensitivity,
                    enabled = state.isEnabled,
                    steps = 3 /* 5 possible values - steps are in ]0, 1[ */,
                    onValueChange = onSensitivityChanged
                )
            } else {
                PreferenceText(title = "Rageshaking is not supported by your device")
            }
        }
    }
}

@Composable
@Preview
fun RageshakePreferencesPreview() {
    RageshakePreferencesView(RageshakePreferencesState(isEnabled = true, isSupported = true, sensitivity = 0.5f))
}