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

package io.element.android.x.features.rageshake.bugreport

import android.os.Parcelable
import io.element.android.x.architecture.Async
import kotlinx.parcelize.Parcelize

data class BugReportState(
    val formState: BugReportFormState = BugReportFormState.Default,
    val hasCrashLogs: Boolean = false,
    val screenshotUri: String? = null,
    val sendingProgress: Float = 0F,
    val sending: Async<Unit> = Async.Uninitialized,
) {
    val submitEnabled =
        formState.description.length > 10 && sending !is Async.Loading
}

@Parcelize
data class BugReportFormState(
    val description: String,
    val sendLogs: Boolean,
    val sendCrashLogs: Boolean,
    val canContact: Boolean,
    val sendScreenshot: Boolean

): Parcelable {
    companion object {
        val Default = BugReportFormState(
            description = "",
            sendLogs = true,
            sendCrashLogs = true,
            canContact = false,
            sendScreenshot = false
        )
    }
}