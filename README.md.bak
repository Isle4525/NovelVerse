# 📚 NovelVerse - Backend для чтения новелл

Добро пожаловать в **NovelVerse**! Это серверная часть приложения для чтения новелл, построенная на Java, Spring Boot и PostgreSQL.

## 🛠 Технологии
- **Java 17** + **Maven**
- **Spring Boot 3** (Data JPA, Web)
- **PostgreSQL** (База данных)
- **Swagger (SpringDoc)** (Документация API)
- **Docker & Docker Compose** (Контейнеризация)

---

## 🚀 Как запустить проект (Docker)

Вам не нужно устанавливать Java или PostgreSQL отдельно. Достаточно установленного Docker.

1.  **Сборка и запуск:**
    Откройте терминал в папке проекта и выполните:
    ```bash
    docker-compose up --build
    ```
    *Эта команда скачает нужные образы, соберет проект и запустит базу данных вместе с приложением.*

2.  **Проверка работы:**
    После запуска перейдите в браузере по адресу:
    - **Swagger UI:** [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html) — здесь можно тестировать API (отправлять запросы GET, POST и т.д.).
    - **API Docs:** [http://localhost:8080/api-docs](http://localhost:8080/api-docs) — JSON описание вашего API.

3.  **Остановка:**
    Чтобы остановить контейнеры, нажмите `Ctrl + C` в терминале или выполните:
    ```bash
    docker-compose down
    ```

---

## 🐙 Работа с Git (Инструкция для новичков)

### 1. Как склонировать проект
Если вы хотите скачать проект на другой компьютер:
```bash
git clone <ссылка_на_ваш_репозиторий>
cd NovelVerse
```

### 2. Работа с ветками (Branches)
Всегда лучше делать изменения в новой ветке, чтобы не "сломать" основную (`main`).

*   **Создать новую ветку и перейти в неё:**
    ```bash
    git checkout -b feature/my-new-feature
    ```
*   **Переключиться на существующую ветку:**
    ```bash
    git checkout main
    ```

### 3. Как сохранить изменения (Commit)
Когда вы написали код и хотите его сохранить локально:

1.  **Посмотреть измененные файлы:**
    ```bash
    git status
    ```
2.  **Добавить файлы в индекс (подготовить к сохранению):**
    ```bash
    git add .
    ```
3.  **Создать коммит (сохранить):**
    ```bash
    git commit -m "Добавил новую модель для глав новелл"
    ```

### 4. Как отправить изменения в интернет (Push)
Чтобы ваши коллеги или GitHub увидели код:
```bash
git push origin feature/my-new-feature
```

---

## 📂 Структура проекта
- `src/main/java/com/novelverse/model` — описание таблиц базы данных (Сущности).
- `src/main/java/com/novelverse/repository` — интерфейсы для работы с БД.
- `src/main/java/com/novelverse/controller` — точки доступа (Endpoints) для вашего API.
- `src/main/resources/application.properties` — настройки подключения к БД.
- `Dockerfile` & `docker-compose.yml` — инструкции для запуска в контейнерах.

---

## 📝 Примеры API запросов (через cURL или Swagger)

**Добавить новеллу:**
```bash
curl -X POST http://localhost:8080/api/novels \
-H "Content-Type: application/json" \
-d '{"title": "Моя первая новелла", "author": "Автор", "description": "Описание...", "content": "Текст первой главы..."}'
```

**Получить все новеллы:**
```bash
curl http://localhost:8080/api/novels
```
