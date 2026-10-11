<div align="center">

<img src="docs/logo.svg" width="96" alt="Compose Bottombar logo" />

# Compose Bottombar

**A collection of beautiful, animated, customizable bottom bars for Jetpack Compose.**

Drop one in, pass your items, and get a polished bar that looks right in light and dark themes, with a colors API for when you want your own palette.

![Kotlin](https://img.shields.io/badge/Kotlin-2.4-7F52FF?logo=kotlin&logoColor=white)
![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-BOM%202026.09-4285F4?logo=jetpackcompose&logoColor=white)
![Min SDK](https://img.shields.io/badge/minSdk-24-3DDC84?logo=android&logoColor=white)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.nameisjayant/compose-bottombar?color=5B3CE0)](https://central.sonatype.com/artifact/io.github.nameisjayant/compose-bottombar)
![License](https://img.shields.io/badge/license-Apache%202.0-blue)

</div>

---

## Preview

<table>
  <tr>
    <th width="50%">☀️ Light</th>
    <th width="50%">🌙 Dark</th>
  </tr>
  <tr>
    <td colspan="2" align="center"><b>Halo</b>: a flat bar with a raised, ringed orb in the middle</td>
  </tr>
  <tr>
    <td valign="top"><img src="docs/gifs/halo-light.gif" width="400" alt="Halo bottom bar in light theme, switching between tabs" /></td>
    <td valign="top"><img src="docs/gifs/halo-dark.gif" width="400" alt="Halo bottom bar in dark theme, switching between tabs" /></td>
  </tr>
</table>

## Features

- 🎯 **Ready to use**: pass a list of items and the selected index, and you get a finished bar.
- 🌗 **Light and dark built in**: every bar ships both palettes and follows the system theme by default.
- ✨ **Animated by default**: icon swaps, spring bounces, color fades and bar-specific touches like the Halo burst.
- 🎨 **Customizable**: change any color through `XxxBottomBarDefaults.lightColors(...)` / `darkColors(...)`, plus label style and window insets.
- ♿ **Accessible**: items are exposed as selectable tabs in a selectable group, so TalkBack reads them correctly.
- 🪶 **Light dependencies**: only Compose UI, Foundation and Animation. No Material, icons or navigation library is forced on your app.
- 🔒 **Explicit API mode**, so the public API is exactly what is meant to be public.

## Bottom bars

| Bar | Composable | Highlights |
|---|---|---|
| **Halo** | `HaloBottomBar` | A flat bar with a raised, ringed center orb. Outlined/filled icon swap, spring bounce on selection, and a ring that ripples out of the orb when it is selected |

More bars are on the way.

## Installation

Compose Bottombar is published on [Maven Central](https://central.sonatype.com/artifact/io.github.nameisjayant/compose-bottombar). No extra repository is needed, just make sure `mavenCentral()` is listed (new Android projects include it by default):

```kotlin
// settings.gradle.kts
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}
```

Then add the dependency:

```kotlin
// app/build.gradle.kts
dependencies {
    implementation("io.github.nameisjayant:compose-bottombar:0.1.0")
}
```

<details>
<summary>Version catalog</summary>

```toml
[versions]
composeBottombar = "0.1.0"

[libraries]
compose-bottombar = { group = "io.github.nameisjayant", name = "compose-bottombar", version.ref = "composeBottombar" }
```

</details>

## Usage

### Halo bottom bar

Each `HaloBarItem` takes a label, an icon, and an optional `selectedIcon` (usually the filled twin of the outlined icon). The **middle item becomes the halo**, so pass an odd number of items (3, 5, 7…).

```kotlin
val items = listOf(
    HaloBarItem("Home", Icons.Outlined.Home, Icons.Filled.Home),
    HaloBarItem("Search", Icons.Outlined.Search, Icons.Filled.Search),
    HaloBarItem("Store", Icons.Outlined.Storefront, Icons.Filled.Storefront), // the halo
    HaloBarItem("Cart", Icons.Outlined.ShoppingCart, Icons.Filled.ShoppingCart),
    HaloBarItem("Profile", Icons.Outlined.Person, Icons.Filled.Person),
)

var selectedIndex by rememberSaveable { mutableIntStateOf(0) }

Scaffold(
    bottomBar = {
        HaloBottomBar(
            items = items,
            selectedIndex = selectedIndex,
            onItemClick = { selectedIndex = it },
        )
    },
) { innerPadding ->
    // Your screen content
}
```

> Any `ImageVector` works. The icons above come from `material-icons-extended`; the sample app draws its own.

The halo is a normal tab: selecting it swaps to its `selectedIcon`, and a ring ripples out of the orb. `onItemClick` also fires when the user taps the tab that is already selected, so you can scroll to the top or pop to the tab's root.

### Light and dark

By default the bar follows `isSystemInDarkTheme()`. If your app has its own theme toggle, pass it in:

```kotlin
HaloBottomBar(
    items = items,
    selectedIndex = selectedIndex,
    onItemClick = { selectedIndex = it },
    colors = HaloBottomBarDefaults.colors(darkTheme = isDarkTheme),
)
```

### Custom colors

Start from either palette and override only what you need:

```kotlin
HaloBottomBar(
    items = items,
    selectedIndex = selectedIndex,
    onItemClick = { selectedIndex = it },
    colors = HaloBottomBarDefaults.lightColors(
        containerColor = Color(0xFFFFF8F0),
        selectedContentColor = Color(0xFFE85D3A),
        unselectedContentColor = Color(0xFFB8A99A),
        unselectedLabelColor = Color(0xFFB8A99A),
        haloColor = Color(0xFFE85D3A),
        haloRingColor = Color(0xFFFFC9B5),
        haloContentColor = Color.White,
    ),
)
```

| Color | Used for |
|---|---|
| `containerColor` | The bar background |
| `selectedContentColor` | Icon and label of the selected side tab |
| `unselectedContentColor` | Icons of the other side tabs |
| `unselectedLabelColor` | Labels of the other side tabs |
| `haloColor` | Fill of the center orb |
| `haloRingColor` | Ring around the orb, and the burst when it is selected |
| `haloContentColor` | Icon inside the orb |

### Label style and insets

```kotlin
HaloBottomBar(
    items = items,
    selectedIndex = selectedIndex,
    onItemClick = { selectedIndex = it },
    labelStyle = HaloBottomBarDefaults.LabelStyle.copy(fontSize = 12.sp, fontFamily = MyFont),
    // The bar pads itself for the navigation bar by default. Pass zero insets when the parent already does.
    windowInsets = WindowInsets(0.dp),
)
```

The bar is `BarHeight` (64dp) tall, and the halo rises `HaloOverhang` (22dp) above it, so leave that much room above the bar for the orb. `HaloBottomBarDefaults` also exposes `HaloSize`, `HaloRingWidth`, `IconSize` and `HaloIconSize`.

### With Navigation 3

The sample app gives each tab its own back stack with [Navigation 3](https://developer.android.com/guide/navigation/navigation-3), and switching tabs keeps every stack intact. The wiring is just:

```kotlin
HaloBottomBar(
    items = tabs.map { it.item },
    selectedIndex = tabs.indexOfFirst { it.route == navigationState.topLevelRoute },
    onItemClick = { index -> navigator.navigate(tabs[index].route) },
)
```

See [`HaloBottomBarDemo.kt`](app/src/main/java/com/nameisjayant/composebottombar/halo/HaloBottomBarDemo.kt) for the full setup.

## Sample app

The `:app` module is a catalog with a live mini preview of every bar. Open a bar to try it in a real multi-tab app with per-tab back stacks, and use the sun/moon toggle to switch between light and dark.

```bash
./gradlew :app:installDebug
```

## Project structure

```
ComposeBottombar/
├── bottombar/   # The library: one package per bar (e.g. halo/HaloBottomBar.kt)
├── app/         # Catalog + demo app (Navigation 3, per-tab back stacks)
├── docs/gifs/   # README GIFs, rendered off-device
└── scripts/     # record-bottombar-gifs.sh
```

## Contributing

Issues and pull requests are welcome. When you add a bar:

1. Add the bar in its own package in `:bottombar`, with an `XxxBottomBarColors` class and `XxxBottomBarDefaults` (light and dark palettes).
2. Add a `BottomBarType` entry, a route in `AppNavigation` and a preview in `HomeScreen` in `:app`.
3. Add a light and a dark test to `RecordBottomBarGifs` in `:app`, run `./scripts/record-bottombar-gifs.sh` to render the GIFs into `docs/gifs/` (needs `ffmpeg`), and add a row to the Preview table above (light on the left, dark on the right).

## Publishing a release

The `:bottombar` module is published with the [vanniktech maven-publish plugin](https://github.com/vanniktech/gradle-maven-publish-plugin). The Central Portal token and the GPG signing key live in `~/.gradle/gradle.properties`, never in this repo:

```properties
mavenCentralUsername=<portal token username>
mavenCentralPassword=<portal token password>
signingInMemoryKeyId=<last 8 chars of the key id>
signingInMemoryKeyPassword=<key passphrase>
signingInMemoryKey=<ascii-armored private key, on one line>
```

To release:

1. Bump `VERSION_NAME` in `gradle.properties`.
2. Optionally test the build locally with `./gradlew :bottombar:publishToMavenLocal`.
3. Run `./gradlew :bottombar:publishToMavenCentral`. The release is published automatically and shows up on Maven Central within about 30 minutes.

<div align="center">

Licensed under the [Apache License 2.0](LICENSE).

Made with ❤️ and Jetpack Compose by [Jayant Kumar](https://github.com/nameisjayant)

</div>
