# KHAIRUL MAP

Android application starter built with Kotlin, Jetpack Compose, Google Maps SDK and Google Mobile Ads.

## Build APK without a PC
GitHub Actions builds a debug APK in the cloud. Open the **Actions** tab, select **Build Android APK**, run the workflow, then download the `KHAIRUL-MAP-debug-apk` artifact when the run succeeds.

## Required GitHub secret
Add repository secret `MAPS_API_KEY` (Settings → Secrets and variables → Actions). Create the key in Google Cloud, enable Maps SDK for Android, and restrict it to package `com.khairul.map` plus the Maps SDK. Do not commit the key to source control.

Google Maps requires a billing-enabled Google Cloud project. Review current pricing and usage limits before enabling billing.

## Ads
The app uses official Google test banner and test interstitial IDs during development. The real AdMob App ID supplied by the owner is set in the manifest.

Owner's production ad units for later activation (after testing and AdMob review):
- Banner: `ca-app-pub-6620088049494024/5943946370`
- Interstitial: `ca-app-pub-6620088049494024/8378538028`

Do not click your own ads. Keep test ad units during development; switch to the production units only when the app is ready and complies with AdMob policies.

## Current features
- Google map centered on Kuala Lumpur
- Map zoom and pan
- Test AdMob banner
- Button to open a typed destination in Google Maps navigation

Location permission, Places autocomplete, and in-app turn-by-turn navigation are not yet implemented.
