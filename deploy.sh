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

echo "Uploading all zip packages in ${PKGS_DIR} to ${CRX_URL}"

for filename in "$PKGS_DIR"/*; do
    echo "Uploading ${filename}"
    curl -v --fail -u "${USER}:${PASSWORD}" -F file=@"${filename}" -F name="${filename}" -F force=true -F install=true "http://${CRX_HOST_PORT}/crx/packmgr/service.jsp"
    # This does not work. I'm leaving it here to show I tried to use the plugin.
    #mvn content-package:install -Dvault.targetURL="http://${CRX_HOST_PORT}" -Dvault.file="${filename}" -Dvault.userId="${USER}" -Dvault.password="${PASSWORD}" -Dvault.verbose="true" -Dvault.failOnError="true" -Dvault.useProxy="false" -Dvault.install="false" --settings ../maven-settings-ci.xml
done