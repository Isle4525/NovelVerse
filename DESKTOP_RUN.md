# NovelVerse Desktop

## Quick start

- **Windows**: Double-click `launch-desktop.bat`
- **Linux / macOS**: Run `./launch-desktop.sh` (or `./mvnw javafx:run`)

This starts a single standalone desktop application that:

- launches the embedded Spring Boot backend on port `8080`
- starts the raw Socket chat server on port `9090`
- embeds and displays the full web UI inside a JavaFX `WebView`
- provides a native, low-latency Socket Chat client in a dedicated desktop tab

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
