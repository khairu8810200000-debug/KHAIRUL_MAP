# KHAIRUL MAP

Android application starter built with Kotlin, Jetpack Compose, Google Maps SDK and Google Mobile Ads.

## Build APK without a PC
GitHub Actions builds a debug APK in the cloud. Open the **Actions** tab, select **Build Android APK**, run the workflow, then download the `KHAIRUL-MAP-debug-apk` artifact when the run succeeds.

## Required GitHub secret
Add repository secret `MAPS_API_KEY` (Settings → Secrets and variables → Actions). Create the key in Google Cloud, enable Maps SDK for Android, and restrict it to package `com.khairul.map` plus the Maps SDK. Do not commit the key to source control.

Google Maps requires a billing-enabled Google Cloud project. Review current pricing and usage limits before enabling billing.

## Ads
The app uses the official Google test banner and test interstitial IDs while developing. The real AdMob App ID supplied by the owner is set in the manifest. Do not use live ad unit IDs for development or click your own ads. After testing and AdMob app review, replace test ad unit IDs with the owner's live units.

## Current features
- Google map centered on Kuala Lumpur
- Map zoom and pan
- Test AdMob banner
- Button to open a typed destination in Google Maps navigation

Location permission, Places autocomplete, and in-app turn-by-turn navigation are not yet implemented.
