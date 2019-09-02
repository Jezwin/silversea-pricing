#! /bin/bash

set -e

RELEASE="${1:-2.1.S13-SNAPSHOT}"

echo "Building and testing silversea.com mono-repo version: ${RELEASE}"
mvn install --settings maven-settings-ci.xml -Drevision="${RELEASE}"

echo "Copying packages to packages directory"
rm -rf crx-packages
mkdir crx-packages
cp "silversea-com/silversea-com-configs/target/silversea-com-configs-${RELEASE}.zip" crx-packages/
cp "silversea-com/silversea-com-ui/target/silversea-com-ui-${RELEASE}.zip" crx-packages/
cp "silversea-ssc/silversea-ssc-ui/target/silversea-ssc-ui-${RELEASE}.zip" crx-packages/
