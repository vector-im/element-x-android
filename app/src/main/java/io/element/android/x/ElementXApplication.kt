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

package io.element.android.x

import android.app.Application
import androidx.startup.AppInitializer
import io.element.android.x.core.di.DaggerComponentOwner
import io.element.android.x.core.di.bindings
import io.element.android.x.di.AppBindings
import io.element.android.x.di.AppComponent
import io.element.android.x.di.DaggerAppComponent
import io.element.android.x.di.SessionComponentsOwner
import io.element.android.x.initializer.CoilInitializer
import io.element.android.x.initializer.CrashInitializer
import io.element.android.x.initializer.MatrixInitializer
import io.element.android.x.initializer.MavericksInitializer
import io.element.android.x.initializer.TimberInitializer

class ElementXApplication : Application(), DaggerComponentOwner {

    private lateinit var appComponent: AppComponent
    private var sessionComponentsOwner: SessionComponentsOwner? = null

    override val daggerComponent: Any
        get() = listOfNotNull(sessionComponentsOwner?.activeSessionComponent, appComponent)

    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerAppComponent.factory().create(applicationContext)
        sessionComponentsOwner = bindings<AppBindings>().sessionComponentsOwner()
        AppInitializer.getInstance(this).apply {
            initializeComponent(CrashInitializer::class.java)
            initializeComponent(TimberInitializer::class.java)
            initializeComponent(MatrixInitializer::class.java)
            initializeComponent(CoilInitializer::class.java)
            initializeComponent(MavericksInitializer::class.java)
        }
    }
}
