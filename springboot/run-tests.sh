#!/usr/bin/env bash
set -euo pipefail
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
if [ -f "$SCRIPT_DIR/.env" ]; then
  export $(grep -v '^#' "$SCRIPT_DIR/.env" | xargs)
fi
if [ -z "${JAVA_HOME:-}" ] || [ ! -x "$JAVA_HOME/bin/java" ]; then
  export JAVA_HOME=$(/usr/libexec/java_home -v 17)
fi
export SPRING_PROFILES_ACTIVE=${SPRING_PROFILES_ACTIVE:-local-db}
(cd "$SCRIPT_DIR/.." && ./gradlew :springboot:test)
