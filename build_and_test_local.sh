#! /bin/bash

set -e

RELEASE="${1:-2.1.S13-SNAPSHOT}"

echo "Building and testing silversea.com mono-repo version: ${RELEASE}"

echo "Building silversea aem parent...."
cd silversea-aem-parent && mvn clean install

echo "Building ssc-api-client"
cd ../ssc-api-client && mvn clean install

echo "Building and testing silversea-com"
cd ../silversea-com && mvn --settings ../maven-settings-ci.xml clean install -Drevision="${RELEASE}"

echo "Building and testing silversea-ssc"
cd ../silversea-ssc && mvn --settings ../maven-settings-ci.xml clean install -Drevision="${RELEASE}"

echo "Copying packages to packages directory"
cd ../
rm -rf crx-packages
mkdir crx-packages
cp "silversea-com/silversea-com-configs/target/silversea-com-configs-${RELEASE}.zip" crx-packages/
cp "silversea-com/silversea-com-ui/target/silversea-com-ui-${RELEASE}.zip" crx-packages/
cp "silversea-ssc/silversea-ssc-ui/target/silversea-ssc-ui-${RELEASE}.zip" crx-packages/
