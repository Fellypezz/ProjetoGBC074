#!/bin/bash

PORTA=$1
if [ -z "$PORTA" ]; then
  echo "Uso: ./server.sh <porta>"
  exit 1
fi

# Executa o servidor com a porta definida
java -cp "bin:lib/*" br.ufu.facom.gbc074.kvs.Main $PORTA
