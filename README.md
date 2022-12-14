# element-x-android-poc

Proof Of Concept to run a Matrix client on Android devices using the Matrix Rust Sdk and Jetpack compose.

The plan is [here](https://github.com/vector-im/element-x-android-poc/issues/1)!


### Modules

This Android project is a multi modules project.

- `app` module is the Android application module. Other modules are libraries;
- `features` modules contain some UI and can be seen as screen of the application;
- `libraries` modules contain classes that can be useful for other modules to work.

A few details about some modules:

- `libraries-core` module contains utility classes;
- `libraries-designsystem` module contains Composables which can be used across the app (theme, etc.);
- `libraries-elementresources` module contains resource from Element Android (mainly strings);
- `libraries-matrix` module contains wrappers around the Matrix Rust SDK.

Here is the current module dependency graph:

<!-- To update this graph, run `./tools/docs/generateModuleGraph.sh` (one day the CI will do it hopefully). -->
<img src=./docs/images/module_graph.png width=800 />

