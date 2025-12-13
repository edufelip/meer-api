#!/usr/bin/env bash
set -euo pipefail
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ENV_FILE="${ENV_FILE:-.env}"
if [ -f "$SCRIPT_DIR/$ENV_FILE" ]; then
  export $(grep -v '^#' "$SCRIPT_DIR/$ENV_FILE" | xargs)
fi
if [ -z "${JAVA_HOME:-}" ] || [ ! -x "$JAVA_HOME/bin/java" ]; then
  export JAVA_HOME=$(/usr/libexec/java_home -v 17)
fi
export SPRING_PROFILES_ACTIVE=${SPRING_PROFILES_ACTIVE:-prod}
(cd "$SCRIPT_DIR/.." && ./gradlew :springboot:bootRun)
