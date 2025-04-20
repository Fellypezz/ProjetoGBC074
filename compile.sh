#!/bin/bash

# Diretório base do projeto
SRC_DIR="src/main/java"
BIN_DIR="bin"

# Cria pasta de binários
mkdir -p $BIN_DIR

# Compila todos os arquivos Java
echo "Compilando arquivos Java..."
javac -d $BIN_DIR -cp "lib/*" $(find $SRC_DIR -name "*.java")

echo "Compilação concluída."
