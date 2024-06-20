# EcoJourney Mobile (Android) Application

## Background

EcoJourney Mobile was developed out of a pressing need to address the growing environmental challenges facing our planet. Recognizing the power of technology in change, we aimed to build a platform that not only educates but also empowers users to take actionable steps towards sustainability through impactful knowledge from news and article. By harnessing the convenience of mobile application, EcoJourney aspires to cultivate a community of environmentally conscious individuals dedicated to making a positive impact on the world.

## Project Plan

-   [x] Preparing architecture and the design concept
-   [x] Design UI/UX for high fidelity mockup
-   [x] UI implementation (XML Layout) and import dependencies
-   [x] Create authentication and article feature
-   [x] Integrate and implement CameraX for the waste classification feature
-   [x] Testing
-   [x] UI and performance improvements

## Features

-   Waste Detection and Classification
-   Article and News Recommendation
-   Waste Detection History

## Minimum Requirement

-   Android Studio Jellyfish | 2023.3.1 Patch 1

## Installation Instructions

1. Fork this repository and clone the forked repository into your local development directory

```shell
git clone https://github.com/<your_profile>/EcoJourney-Mobile.git
```

2. Navigate into the cloned repository

```shell
cd EcoJourney-Mobile
```

3. Build the project by command or just hit run button to build and run the project immediately
```shell
./gradlew build
```

4. The application will run on the emulator or real device according to what was previously selected

## Libraries

-  Retrofit: HTTP client used to handle REST API for Android application
- TFLite: Tensorflow Lite for integrating ML models in Android application
- CameraX: Custom camera for Android application 
- Datastore: Data storage to shared preferences for Android application
- Viewmodel: Persistence UI-related data provider for Android application
- Livedata: Lifecycle-aware data holder that can be observed within lifecycle
- KTX: Kotlin extension to make concise the Android application development
- GSON Converter: JSON converter from web service to Kotlin objects
- Glide: Image loading, caching, and displaying for Android application
- Kotlinx Coroutine: Asynchronous support in kotlin for Android application
