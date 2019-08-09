#! /bin/bash

set -e

HOST="${AEM_HOST:-localhost}"

echo "Building and deploying to ${HOST}"

__dir="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
bash ${__dir}/build_and_deploy.sh http://"${HOST}":4502 admin admin

