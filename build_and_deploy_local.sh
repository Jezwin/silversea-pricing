#! /bin/bash

set -e

__dir="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
bash ${__dir}/build_and_deploy.sh http://localhost:4502 admin admin

