#!/bin/sh
set -e

echo "Waiting for MinIO..."
until mc alias set local "http://${BUCKET_HOST}:${BUCKET_PORT}" "${BUCKET_ACCESS_KEY}" "${BUCKET_ACCESS_SECRET}"; do
  sleep 1
done

echo "Creating bucket if it doesn't exist..."
mc mb --ignore-existing "local/${BUCKET_NAME}"

echo "Setting bucket policy..."
# mc anonymous set none "local/${BUCKET_NAME}"

echo "Uploading seed files (if any)..."
if [ -d /seed-files ] && [ "$(ls -A /seed-files 2>/dev/null)" ]; then
  mc cp --recursive /seed-files/* "local/${BUCKET_NAME}/"
fi

echo "MinIO init completed."