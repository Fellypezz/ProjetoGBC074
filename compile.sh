#!/bin/bash

echo "🔧 Iniciando compilação do projeto com Maven..."

# Verifica se o Maven está instalado
if ! command -v mvn &> /dev/null
then
    echo " Maven não está instalado. Use: sudo apt install maven"
    exit 1
fi

# Compila o projeto usando Maven
mvn clean compile

if [ $? -eq 0 ]; then
    echo " Compilação concluída com sucesso!"
else
    echo " Erro durante a compilação!"
    exit 1
fi