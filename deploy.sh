#! /bin/bash

set -e

CRX_HOST_PORT=$1
PKGS_DIR=$2
USER=$3
PASSWORD=$4

if [ ! -d "$PKGS_DIR" ]; then
    echo "Package directory: ${PKGS_DIR} does not exist. Aborting."
    exit 1;
fi

echo "Uploading all packages in ${PKGS_DIR} to ${CRX_URL}"

for filename in "$PKGS_DIR"/*; do
    echo "Uploading ${filename}"
    curl --fail -u "${USER}:${PASSWORD}" -F file=@"${filename}" -F name="${filename}" -F force=true -F install=true "http://${CRX_HOST_PORT}/crx/packmgr/service.jsp"
done

echo "Checking packages installed correctly..."

curl --fail -o installed-packages.txt -u "${USER}:${PASSWORD}" "http://${CRX_HOST_PORT}/crx/packmgr/service.jsp?cmd=ls"

upload_failed() {
    echo "Upload failed."
    exit 1;
}

for filename in "$PKGS_DIR"/*; do
    echo "Checking ${filename} was correctly installed..."
    pkg_file="$(basename "$filename")"
    grep -A2 -B2 "<downloadName>${pkg_file}</downloadName>" installed-packages.txt || upload_failed
    echo "${filename} was correctly installed..."
done