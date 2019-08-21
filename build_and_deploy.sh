#! /bin/bash

set -e

AEM_URL=$1
PASSWORD=$2
USER=$3

echo "Building and Deploying to: ${AEM_URL}"

echo "Building silversea aem parent...."
cd silversea-aem-parent && mvn clean install

echo "Building ssc-api-client"
cd ../ssc-api-client && mvn clean install

echo "Building and deploying silversea-com"
cd ../silversea-com && mvn --settings ../maven-settings-ci.xml clean install -Pauto-install-package -Dcq.password="${PASSWORD}" -Dcq.user="${USER}" -Dcq.url="${AEM_URL}"

echo "Building and deploying silversea-ssc"
cd ../silversea-ssc && mvn --settings ../maven-settings-ci.xml clean install -Pauto-install-package -Dcq.password="${PASSWORD}" -Dcq.user="${USER}" -Dcq.url="${AEM_URL}"
