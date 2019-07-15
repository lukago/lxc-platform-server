#!/usr/bin/env bash

mvn clean install
docker build -t lxc-platform-server .
docker-compose -f docker-compose.local.yml up
