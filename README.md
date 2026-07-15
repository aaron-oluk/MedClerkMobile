# MedClerkMobile

MedClerkMobile is the native Android client for [MedClerk](../MedClerk), a clinical education and competency assessment platform for medical students. It targets the student's day-to-day workflow — reviewing the clinical library, recording logbook encounters during a rotation, and checking assessment results and feedback — against the same Laravel REST API used by the web app.

The UI design (teal/neutral theme, cards, chips, progress rings) is ported from the `workspace-medclerk` Next.js prototype, which serves as a visual reference only; this native app is the actual product.

## Tech stack

- Kotlin, Jetpack Compose, Material 3
- MVVM: `ViewModel` + `StateFlow` per screen, a small hand-rolled `AppContainer` service locator (no DI framework)
- Networking: Retrofit + OkHttp, `kotlinx.serialization` for JSON
- Navigation: Navigation Compose
- Local storage: `EncryptedSharedPreferences` for the auth token and cached user identity; the API base URL is a `BuildConfig` field

## Features

- **Login**: email/password against `POST /api/auth/login`, Sanctum token stored encrypted on-device
- **Home**: greeting header, active rotation card (progress ring, encounter counts, "Record new encounter"), today's assessment/feedback counts, and a competency snapshot (strongest/weakest clinical system by mastery %)
- **Library**: all clinical systems with a mastery-percent bar per system, drilling into clinical signs (interpretation, technique, diagnostic relevance, red flags, media) and skills (structured procedure steps, equipment, competency codes, mastery %)
- **Logbook**: list of recorded encounters and a form to record a new one against the student's active rotation
- **Results**: assessment history and scores
- **Feedback**: feedback entries from supervisors (strengths, areas to improve, follow-up date)
- **Rotations**: full list of the student's clerkship placements with status and dates
- **Profile**: account summary (name, email, role, institution/department), stats (average score, encounters, feedback, rotations), overall competency ring, and sign-out

## Requirements

- Android Studio (Ladybug or newer) with an Android SDK matching `compileSdk`/`targetSdk` 36

## Setup

The app talks to the backend via the `API_BASE_URL` build config field in `app/build.gradle.kts`, which points at the hosted MedClerk backend by default:

```kotlin
buildConfigField("String", "API_BASE_URL", "\"https://medclerk.aloflux.com/api/\"")
```

To point at a local backend instead (e.g. `php artisan serve` on your machine), change this to `http://10.0.2.2:8001/api/` when running on the Android emulator (`10.0.2.2` is the emulator's alias for the host machine's `localhost`) or your machine's LAN IP for a physical device. Local HTTP traffic to `10.0.2.2`/`localhost` is allow-listed in `app/src/main/res/xml/network_security_config.xml`; the production domain is HTTPS and needs no such exception.

Build and install on a running emulator or connected device:

```bash
./gradlew assembleDebug
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

Or run directly from Android Studio with the `app` run configuration.

Log in with any of the MedClerk backend's seeded demo accounts (see the backend README), e.g. `student@medclerk.test` / `password`.

## Project structure

- `app/src/main/java/com/example/medclerkmobile/data`: `AppContainer` (service locator), Retrofit `ApiService`, repositories, `kotlinx.serialization` models, `TokenStore`
- `app/src/main/java/com/example/medclerkmobile/ui`: one package per screen (`home`, `library`, `logbook`, `assessments`, `feedback`, `rotations`, `profile`, `auth`, `dashboard`) plus a shared design-system package (`MedCard`, `MedChip`, `ProgressRing`, `SectionTitle`, `ScreenHeader`)
- `app/src/main/java/com/example/medclerkmobile/navigation`: route constants
- `app/src/main/java/com/example/medclerkmobile/MainActivity.kt`: the Navigation Compose graph wiring every screen together

## Tests

```bash
./gradlew test
```
