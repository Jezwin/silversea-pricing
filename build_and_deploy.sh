#! /bin/bash

set -e

AEM_URL=$1
PASSWORD=$2

echo "Building and Deploying to: ${AEM_URL}"

cd silversea-aem-parent && mvn clean install
cd ../silversea-com && mvn --settings ../maven-settings-ci.xml clean install -Pauto-install-package -Dcq.password="${PASSWORD}" -Dcq.user=ci-user -Dcq.url="${AEM_URL}" -DapiClientVersion="[3.0, 4.0]"
