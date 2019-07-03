#!/usr/bin/env bash

mvn clean install
sudo docker build -t lxc-platform-server .
sudo docker-compose -f docker-compose-local.yml up
