#!/bin/bash

PORTA=$1
if [ -z "$PORTA" ]; then
  echo "Uso: ./server.sh <porta>"
  exit 1
fi

mvn exec:java -Dexec.mainClass="br.ufu.facom.gbc074.kvs.Main" -Dexec.args="$PORTA"
