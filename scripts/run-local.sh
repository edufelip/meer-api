#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/.."

source ./springboot/.env.dev
./springboot/run-local.sh