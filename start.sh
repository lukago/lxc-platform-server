#!/usr/bin/env bash

script_name=`basename "$0"`
clearEnv=0
skipIT=0

while getopts "ci" opt; do
  case ${opt} in
    c)
      clearEnv=1
      ;;
    i)
      skipIT=1
      ;;
    \?)
      echo "Invalid option: -$OPTARG" >&2
      echo "Usage: $script_name [optional flags: -c; -i;]"
      exit 1
      ;;
  esac
done

if [[ ${clearEnv} == 1 ]]; then
  echo "Clearing docker env..."
  docker stop $(docker ps -aq)
  docker rm $(docker ps -aq)
  rm -r ~/.lxc-platform-db
  mkdir ~/.lxc-platform-db
fi

if [[ ${skipIT} == 1 ]]; then
  echo "> mvn clean install -U -DskipITs"
  mvn clean install -U -DskipITs
else
  echo "mvn clean install -U"
  mvn clean install -U
fi

docker build -t lxc-platform-server .
docker-compose -f docker-compose.local.yml up
