#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/.."

source ./springboot/.env
./springboot/run-local.sh