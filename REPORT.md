# Project Berbox Report

## Environment Variables and API Keys
The project uses the Secrets Gradle Plugin to inject secrets at compile time based on a `.env` file (copied from `.env.example`).
In the `.env.example`, the following secrets are managed:
*   `GEMINI_API_KEY`: Required for Gemini AI API calls. Currently commented out. **Action Required:** You must provide a valid Gemini API Key by uncommenting `# GEMINI_API_KEY=MY_GEMINI_API_KEY` and replacing the placeholder in your actual `.env` file (which is NOT checked into source control) if you are using AI features.
*   `SUPABASE_URL` and `SUPABASE_ANON_KEY`: Default placeholder values are present. **Action Required:** Ensure these point to your actual Supabase instance if you expect real database access.
*   **Firebase/Other Missing Services:** There are no explicit environment variable slots for Firebase, as Firebase configuration uses `google-services.json`.

## Handling `google-services.json` and Keystores
*   **`google-services.json`:** The app utilizes Firebase (evident from `com.google.gms.google-services` plugin and firebase dependencies). The `build.gradle.kts` specifies `MissingGoogleServicesStrategy.WARN`, meaning debug builds will successfully compile even if the `google-services.json` file is absent, but it will print a warning.
    *   **Action Required for Production:** To ensure full Firebase capabilities work correctly in release builds, you *must* place the valid `google-services.json` provided by the Firebase console into the `app/` directory (i.e. `app/google-services.json`).
*   **Release Keystore:** The release build configuration checks for `KEYSTORE_PATH`, `STORE_PASSWORD`, and `KEY_PASSWORD` as system environment variables. If missing, it defaults to a local file `${rootDir}/my-upload-key.jks`.
    *   **Action Required for Production:** You need to either provide those environment variables during CI build/release steps, or keep a local `my-upload-key.jks` in the root folder when building locally.

## Extracting the Generated APK
### 1. Via GitHub Actions (Automated)
Every time you push to `main` or `master` (or open a Pull Request), GitHub Actions will automatically run the build.
1.  Go to your repository on GitHub.
2.  Navigate to the **Actions** tab.
3.  Click on the latest successful workflow run.
4.  Scroll down to the **Artifacts** section at the bottom.
5.  Click on `app-debug` to download the zip file containing the generated `.apk`.

### 2. Locally (Manual)
To build the app yourself on your machine:
1.  Ensure you have Java 17 and Android Studio (or Android SDK) installed.
2.  Copy `.env.example` to `.env` in the root directory: `cp .env.example .env`
3.  Run the build command: `./gradlew assembleDebug`
4.  The generated APK will be located at: `app/build/outputs/apk/debug/app-debug.apk`