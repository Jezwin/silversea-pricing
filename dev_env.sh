#! /bin/bash

set -ex

docker-compose up --no-recreate -d
docker-compose exec silversea /bin/bash 