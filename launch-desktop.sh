#!/usr/bin/env bash
set -e

DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" >/dev/null 2>&1 && pwd)"
cd "$DIR"

echo "Starting NovelVerse Desktop..."

if [ -f "./mvnw" ]; then
    chmod +x ./mvnw
    ./mvnw -q -DskipTests javafx:run
elif command -v mvn >/dev/null 2>&1; then
    mvn -q -DskipTests javafx:run
else
    echo "Error: Maven or mvnw was not found."
    echo "Please install Maven or ensure ./mvnw is present."
    exit 1
fi
