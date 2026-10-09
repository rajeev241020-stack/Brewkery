# Brewkery – Android Developer Take-Home

A native Android coffee & bakery ordering app built with **Kotlin** and **Jetpack Compose**, powered by a single static JSON API.

**Flow:** Menu → Item Detail → (Add to Cart) → Cart → (Place Order) → Order Status

## Features
- **Menu / Home:** store header with cart badge, store info card (delivery time and flat fee), search, category chips, and item cards (thumbnail, badge, title, rating, base price). After an order is placed, an active-order tracking banner appears.
- **Item Detail:** hero image, rating, prep time, calories, ingredients, and dynamic customizations (`sizes`, `milk_options`, `sugar_levels`) with live price recalculation and a quantity stepper.
- **Cart:** items with quantity controls and remove, subtotal + $2.50 delivery + 8% tax, and total.
- **Order Status:** places the order, clears the cart, generates a ticket ID, and shows `PREPARING` status.
- Loading state and error message with retry for network calls.
- Images loaded from URLs with Coil.
- Unit test for cart price/tax/total calculation.
- No local database (the cart resets when the app closes).

## Tech Stack
- Kotlin, Jetpack Compose (Material 3), Navigation Compose
- Retrofit + Gson + Kotlin Coroutines
- Coil (image loading)
- MVVM with a ViewModel exposing `StateFlow` (`Loading` / `Error` / `Success`)

## API
- Menu: `https://raw.githubusercontent.com/VivekShah138/Brewkery/main/data.json`
- Item: `https://raw.githubusercontent.com/VivekShah138/Brewkery/main/api/items/{id}.json`

## How to Run
1. Open the project in Android Studio and let Gradle sync.
2. Run on an emulator or device (needs internet access).
3. Build a debug APK: **Build → Build Bundle(s) / APK(s) → Build APK(s)**, or run `./gradlew assembleDebug`.
   The APK is at `app/build/outputs/apk/debug/`.
4. Run unit tests: `./gradlew testDebugUnitTest`

## How I Used AI
**Tool used:** Claude, for generating the initial code (screens, ViewModel, API layer, unit test) from the assignment screenshots and instructions.

**What I did myself:** set up the Android Studio project and Gradle configuration, resolved the build errors (compileSdk / minSdk version conflicts, KSP and built-in Kotlin issues, wrong auto-imports), ran and tested the app on an emulator, found the UI bugs, and reviewed and pushed the code.

**Prompts I sent (examples):**
1. "Make the same UI as the screenshots, with all screens from the assignment instructions, in Jetpack Compose and Kotlin."
2. "The cart badge is cut off and the last item is hidden behind the navigation bar. Fix it."

**One thing AI got right:** It read the real API JSON first and modelled the data classes (including the nested `customizations`) correctly, so parsing worked right away.

**One thing AI got wrong, and how I fixed it:** The first version of the menu screen had two UI bugs that I only noticed when running it on the emulator: the cart count badge was cut off (it sat inside a clipped circle), and the last item in the list was hidden behind the system navigation bar. I took screenshots, described the problem back to the AI, and the fix was to move the badge into an unclipped parent box and add navigation-bar bottom padding to the list's `contentPadding`. I also had to clean up wrong auto-imported classes in my project (`Outline`, `Badge`) that clashed with my own theme classes.
