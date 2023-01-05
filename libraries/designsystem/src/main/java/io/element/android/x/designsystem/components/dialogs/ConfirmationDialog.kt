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

package io.element.android.x.designsystem.components.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.element.android.x.ui.strings.R as StringR

@Composable
fun ConfirmationDialog(
    title: String,
    content: String,
    modifier: Modifier = Modifier,
    submitText: String = stringResource(id = StringR.string.ok),
    cancelText: String = stringResource(id = StringR.string.action_cancel),
    thirdButtonText: String? = null,
    onSubmitClicked: () -> Unit = {},
    onCancelClicked: () -> Unit = {},
    onThirdButtonClicked: () -> Unit = {},
    onDismiss: () -> Unit = {},
) {
    AlertDialog(
        modifier = modifier,
        onDismissRequest = onDismiss,
        title = {
            Text(text = title)
        },
        text = {
            Text(content)
        },
        dismissButton = {
            Row(
                modifier = Modifier.padding(all = 8.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Column {
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            onCancelClicked()
                        }) {
                        Text(cancelText)
                    }
                    if (thirdButtonText != null) {
                        Button(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                onThirdButtonClicked()
                            }) {
                            Text(thirdButtonText)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Row(
                modifier = Modifier.padding(all = 8.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        onSubmitClicked()
                    }
                ) {
                    Text(submitText)
                }
            }
        },
    )
}

@Composable
@Preview
fun ConfirmationDialogPreview() {
    ConfirmationDialog(
        title = "Title",
        content = "Content",
        thirdButtonText = "Disable"
    )
}
