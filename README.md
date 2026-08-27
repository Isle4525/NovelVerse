# NovelVerse

NovelVerse is a Java OOP project for reading and managing novels.  
The project combines:

- `Spring Boot` backend
- `JDBC + PostgreSQL`
- HTML/CSS/JS frontend
- `Socket` chat on port `9090`
- `JavaFX` desktop wrapper with `WebView`
- PDF import/export using `Apache PDFBox`

The application can run as:

- a regular web app in the browser
- a desktop app where the website opens inside `WebView` and the chat works in a native JavaFX tab

## Main Features

- user registration and login
- novel catalog
- create, edit, delete novels
- create, edit, delete chapters
- chapter image support
- comments on novels
- bookmarks
- upload PDF and split it into chapters
- export chapter to PDF
- socket chat on port `9090`
- desktop UI with JavaFX

## Project Structure

`src/main/java/com/novelverse/novelverse`

- `config`  
  Spring configuration, datasource, executor, chat startup

- `controller`  
  REST endpoints for auth, novels, chapters, comments, bookmarks, PDF

- `service`  
  Business logic

- `repository`  
  JDBC repositories

- `domain`  
  Core OOP models

- `dto`  
  Request DTO classes

- `socket`  
  Raw `ServerSocket` chat implementation

- `desktop`  
  JavaFX desktop launcher, WebView shell, native chat client, desktop proxy

`src/main/resources`

- `static`  
  frontend pages

- `schema.sql`  
  database schema

- `application.properties`  
  Spring configuration

## Architecture

The main backend flow is:

`HTML page -> Controller -> Service -> Repository -> PostgreSQL`

Examples:

- novels:
  - [NovelController.java](C:/Users/end/NovelVerse_final/src/main/java/com/novelverse/novelverse/controller/NovelController.java:1)
  - [NovelService.java](C:/Users/end/NovelVerse_final/src/main/java/com/novelverse/novelverse/service/NovelService.java:1)
  - [NovelRepositoryImpl.java](C:/Users/end/NovelVerse_final/src/main/java/com/novelverse/novelverse/repository/impl/NovelRepositoryImpl.java:1)

- chapters:
  - [ChapterController.java](C:/Users/end/NovelVerse_final/src/main/java/com/novelverse/novelverse/controller/ChapterController.java:1)
  - [ChapterService.java](C:/Users/end/NovelVerse_final/src/main/java/com/novelverse/novelverse/service/ChapterService.java:1)
  - [ChapterRepositoryImpl.java](C:/Users/end/NovelVerse_final/src/main/java/com/novelverse/novelverse/repository/impl/ChapterRepositoryImpl.java:1)

## OOP Part

The project includes:

- classes and objects
- inheritance
- abstraction
- interfaces
- collections

Important files:

- [Content.java](C:/Users/end/NovelVerse_final/src/main/java/com/novelverse/novelverse/domain/Content.java:1)
- [Readable.java](C:/Users/end/NovelVerse_final/src/main/java/com/novelverse/novelverse/domain/Readable.java:1)
- [Novel.java](C:/Users/end/NovelVerse_final/src/main/java/com/novelverse/novelverse/domain/Novel.java:1)
- [Chapter.java](C:/Users/end/NovelVerse_final/src/main/java/com/novelverse/novelverse/domain/Chapter.java:1)

## Database

The project uses PostgreSQL through JDBC.

Database config:

- [DbConfig.java](C:/Users/end/NovelVerse_final/src/main/java/com/novelverse/novelverse/config/DbConfig.java:1)

Current connection:

- database: `novel_db`
- user: `admin`
- password: `1234`
- host: `localhost:5432`

Schema:

- [schema.sql](C:/Users/end/NovelVerse_final/src/main/resources/schema.sql:1)

Tables:

- `users`
- `novels`
- `chapter`
- `comments`
- `bookmarks`

## REST API

### Auth

- `POST /auth/register`
- `POST /auth/login`

### Novels

- `GET /novels`
- `GET /novels/{id}`
- `POST /novels`
- `PUT /novels/{id}`
- `DELETE /novels/{id}`

### Chapters

- `GET /chapters/novel/{id}`
- `GET /chapters/{id}`
- `POST /chapters`
- `PUT /chapters/{id}`
- `DELETE /chapters/{id}`

### Comments

- `GET /comments/novel/{id}`
- `POST /comments`

### Bookmarks

- `GET /bookmarks/{userId}`
- `POST /bookmarks`
- `DELETE /bookmarks`

### PDF

- `POST /pdf/upload`
- `GET /pdf/chapter/{chapterId}`

## Frontend Pages

Frontend files are in:

- [index.html](C:/Users/end/NovelVerse_final/src/main/resources/static/index.html:1)
- [login.html](C:/Users/end/NovelVerse_final/src/main/resources/static/login.html:1)
- [register.html](C:/Users/end/NovelVerse_final/src/main/resources/static/register.html:1)
- [novel.html](C:/Users/end/NovelVerse_final/src/main/resources/static/novel.html:1)
- [read.html](C:/Users/end/NovelVerse_final/src/main/resources/static/read.html:1)
- [profile.html](C:/Users/end/NovelVerse_final/src/main/resources/static/profile.html:1)
- [admin.html](C:/Users/end/NovelVerse_final/src/main/resources/static/admin.html:1)

## PDF Read/Write

The project covers file reading and writing:

- PDF reading: uploaded file is parsed into chapters
- PDF writing: a chapter can be exported and downloaded as a new PDF

Main files:

- [PdfController.java](C:/Users/end/NovelVerse_final/src/main/java/com/novelverse/novelverse/controller/PdfController.java:1)
- [PdfService.java](C:/Users/end/NovelVerse_final/src/main/java/com/novelverse/novelverse/service/PdfService.java:1)

## Threads and Executor

The project uses a shared `ExecutorService` for background tasks.

Config:

- [ExecutorConfig.java](C:/Users/end/NovelVerse_final/src/main/java/com/novelverse/novelverse/config/ExecutorConfig.java:1)

Current implementation:

- fixed thread pool with `8` threads

Used in:

- async PDF parsing
- socket chat startup
- socket client handling

## Socket Chat

The chat uses raw Java sockets, not Spring WebSocket.

Files:

- [ChatServer.java](C:/Users/end/NovelVerse_final/src/main/java/com/novelverse/novelverse/socket/ChatServer.java:1)
- [ClientHandler.java](C:/Users/end/NovelVerse_final/src/main/java/com/novelverse/novelverse/socket/ClientHandler.java:1)
- [ChatServerStartup.java](C:/Users/end/NovelVerse_final/src/main/java/com/novelverse/novelverse/config/ChatServerStartup.java:1)

Port:

- `9090`

## Desktop Mode

Desktop mode uses JavaFX.

Files:

- [NovelVerseDesktopLauncher.java](C:/Users/end/NovelVerse_final/src/main/java/com/novelverse/novelverse/desktop/NovelVerseDesktopLauncher.java:1)
- [NovelVerseDesktopApp.java](C:/Users/end/NovelVerse_final/src/main/java/com/novelverse/novelverse/desktop/NovelVerseDesktopApp.java:1)
- [DesktopSocketChatClient.java](C:/Users/end/NovelVerse_final/src/main/java/com/novelverse/novelverse/desktop/DesktopSocketChatClient.java:1)
- [DesktopWebProxyServer.java](C:/Users/end/NovelVerse_final/src/main/java/com/novelverse/novelverse/desktop/DesktopWebProxyServer.java:1)

Desktop behavior:

- starts Spring Boot backend
- checks backend readiness through `/novels`
- opens website inside `WebView`
- opens native socket chat in a separate tab
- uses a local desktop proxy on `18080` to stabilize `WebView` behavior

## How to Run

### 1. Start PostgreSQL

If you use Docker:

```bash
docker compose up -d
```

File:

- [docker-compose.yaml](C:/Users/end/NovelVerse_final/docker-compose.yaml:1)

### 2. Run as Web App

With Maven wrapper:

```bash
./mvnw spring-boot:run
```

On Windows:

```bat
mvnw.cmd spring-boot:run
```

Then open:

- `http://127.0.0.1:8080`

### 3. Run as Desktop App

Quick start:

- double-click [launch-desktop.bat](C:/Users/end/NovelVerse_final/launch-desktop.bat:1)

Or run:

```bash
mvn -DskipTests javafx:run
```

Desktop app includes:

- website tab
- native socket chat tab
- window controls and custom title bar

## Packaging

For demo app image:

- [build-desktop-image.bat](C:/Users/end/NovelVerse_final/build-desktop-image.bat:1)

For Windows `.exe` style package:

- [build-desktop-exe.bat](C:/Users/end/NovelVerse_final/build-desktop-exe.bat:1)

Extra notes:

- [DESKTOP_RUN.md](C:/Users/end/NovelVerse_final/DESKTOP_RUN.md:1)

## Demo Flow for Presentation

Recommended short demo:

1. Open desktop app
2. Show catalog page
3. Register or login
4. Open `Manage`
5. Create a novel
6. Add a chapter
7. Open novel page
8. Read chapter
9. Export chapter to PDF
10. Open socket chat tab

## Notes

- Browser mode and desktop mode share the same backend and database
- Desktop mode is mainly a presentation-friendly wrapper around the same system
- Chat uses pure sockets, not Spring WebSocket
- JDBC is implemented manually through repositories, not JPA

## Technologies

- Java 17+
- Spring Boot
- JDBC
- PostgreSQL
- JavaFX
- HTML / CSS / JavaScript
- Apache PDFBox
- Docker

