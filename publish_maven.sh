#!/bin/bash

# Load environment variables from .env
if [ -f .env ]; then
  export $(grep -v '^#' .env | xargs)
else
  echo ".env file not found"
  exit 1
fi

# Check if variables are loaded
if [[ -z "$MAVEN_USER" || -z "$MAVEN_PASSWORD" ]]; then
  echo "MAVEN_USER or MAVEN_PASSWORD not set"
  exit 1
fi

./gradlew publishToMavenLocal
