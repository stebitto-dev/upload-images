# Upload Images

The aim of this app is to upload local images to a web server.  
In particular, a free service was used: [Catbox](https://catbox.moe/).

As the app open, user is prompted to choose his country:  
<img src="https://drive.google.com/uc?export=view&id=1f8Yv0OmjWJGVBdDuDkP0bKaROq_nIfQb" width="250" />

After that, user is able to pick images from various sources:

 - Gallery
 - Google Photo
 - Take a picture from Camera.

There is a dedicated button to start the uploading phase:  
<img src="https://drive.google.com/uc?export=view&id=1fJREkkRYeXNwRZU7H5Lw247rNZ2pfUpG" width="250" /> <img src="https://drive.google.com/uc?export=view&id=1fJyVa2WPAmOxdeYLedafAMuNVK0t8im2" width="250" />

## Technical details

The project is built upon few dependencies:

 - [FlowRedux](https://freeletics.github.io/FlowRedux/), a coroutine-based library to implement Redux pattern
 - [Hilt](https://dagger.dev/hilt/), a dependency injection framework backed by Google
 - [Jetpack Compose](https://developer.android.com/jetpack/compose) for UI
 - [Retrofit](https://square.github.io/retrofit/), a type-safe HTTP client
 - [Coil](https://coil-kt.github.io/coil/), an image loading library backed by coroutines
 - [Turbine](https://github.com/cashapp/turbine), testing library for Kotlin Flow
 - [Mockk](https://mockk.io/), mocking library for unit tests.

The architecture is following Redux pattern, using **AppStateMachine** class as single source of truth.

The uploading phase is divided into two states: *PickImages* and *UploadingImages*. In PickImages state, user is able to pick new images from device repeatedly, or remove previously added elements. On the other hand, in UploadingImages state, the list cannot be modified until all uploads are finished.  
Behind the scenes, UploadingImages is leveraging a *Channel* to synchronize the UI. On entering state, a new coroutine is spawned for every image in order to decompose the workflow with parallel uploads. Every time an upload completes, result is sent to the channel. On the other side, channel consumer is going to update the state with new data.

AppStateMachine behavior is tested in **AppStateMachineTest**, while composables have their own instrumented test.  

CI sample can be found in *.github/workflows/ci.yml*.

## Additional info

 - app version: **1.0.0**
 - Android SDK target: **33 (Android 13)**
 - minimum Android SDK supported: **26 (Android 8)**
 - Kotlin version: **1.8.10**
 - Android Gradle plugin version: **7.4.1**
