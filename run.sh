#!/usr/bin/env bash
# Helper script: ./run.sh [build|run|demo|test]
# Needs JDK 17+ (javac and java on your PATH).
set -e
cd "$(dirname "$0")"

build() {
    mkdir -p out
    javac -d out src/parking/*.java test/SimpleTests.java
}

case "${1:-run}" in
    build) build ;;
    run)   build && java -cp out parking.Main ;;
    demo)  build && java -cp out parking.Demo ;;
    test)  build && java -cp out:. SimpleTests ;;
    *) echo "Usage: ./run.sh [build|run|demo|test]" ;;
esac
