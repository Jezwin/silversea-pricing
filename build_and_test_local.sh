#! /bin/bash

set -e

echo "Building silversea aem parent...."
cd silversea-aem-parent && mvn clean install

echo "Building ssc-api-client"
cd ../ssc-api-client && mvn clean install

echo "Building and testing silversea-com"
cd ../silversea-com && mvn --settings ../maven-settings-ci.xml clean install

echo "Building and testing silversea-ssc"
cd ../silversea-ssc && mvn --settings ../maven-settings-ci.xml clean install
