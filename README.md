# KHAIRUL MAP

Android application built with Kotlin, Jetpack Compose, OpenStreetMap (osmdroid), fused GPS location, and Google Mobile Ads.

## Build APK without a PC

GitHub Actions builds a debug APK in the cloud. Open the **Actions** tab, select **Build Android APK**, run the workflow, then download the `KHAIRUL-MAP-debug-apk` artifact when the run succeeds.

## Maps and location

- Map tiles are provided by OpenStreetMap through osmdroid.
- No Google Maps API key or `MAPS_API_KEY` GitHub secret is required.
- The app requests coarse or fine location permission when the user taps **Lokasi Saya**.
- Enable Location/GPS on the Android device and allow the app to access location.
- OpenStreetMap data and tile usage are subject to the OpenStreetMap tile usage policy. Use a compliant tile provider for production-scale traffic.

## Ads

The app uses Google's official test banner and test interstitial ad unit IDs during development. The AdMob application ID in the Android manifest is the production app ID.

Production ad units are documented here for later activation after testing and AdMob review:
- Banner: `ca-app-pub-6620088049494024/5943946370`
- Interstitial: `ca-app-pub-6620088049494024/8378538028`

Do not click your own ads. Keep test ad units during development; switch to production units only when the app is ready and complies with AdMob policies.

## Current features

- OpenStreetMap centered on Kuala Lumpur
- Map zoom and pan
- Search a typed destination by opening OpenStreetMap search in the browser
- Current GPS location button with a map marker
- Test AdMob banner and interstitial
- Map lifecycle is connected to the Android screen lifecycle
