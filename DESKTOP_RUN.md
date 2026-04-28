# NovelVerse Desktop

## Quick start

Double-click:

- `launch-desktop.bat`

This starts one desktop application that:

- launches Spring Boot backend on `8080`
- launches socket chat on `9090`
- opens the website inside JavaFX `WebView`
- opens native chat in a separate desktop tab

## Demo build

To create a desktop app-image for presentation, run:

- `build-desktop-image.bat`

This uses `jpackage` and creates an app image inside:

- `dist/NovelVerseDesktop`

If you want a Windows installer-style package and your local `jpackage` supports it, run:

- `build-desktop-exe.bat`

## Notes

- For quick launch, Maven or a working `mvnw.cmd` is required.
- For app-image packaging, JDK 17+ with `jpackage` is required.
- For `.exe` packaging, local Windows `jpackage` support is required.
