#!/usr/bin/env bash

docker stop $(docker ps -aq)
docker rm $(docker ps -aq)

sudo rm -r /var/lxcdata/db
sudo mkdir /var/lxcdata/db

