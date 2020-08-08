#!/usr/bin/env bash

cd upgrade-campsite-postgresdb/
pwd
ls -la
# execute docker build
docker build -t campsite_pg .

cd -
cd upgrade-campsite/
pwd
ls -la
mvn package
#execute docker build
docker build -t campsite-java-api .
cd -