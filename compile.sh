#!/bin/bash

echo "🔧 Iniciando compilação do projeto..."

# Verifica se Maven está instalado
if ! command -v mvn &> /dev/null
then
    echo "❌ Maven não encontrado. Instale com: sudo apt install maven"
    exit 1
fi

# Limpa e compila o projeto
mvn clean compile

# Verifica se a compilação foi bem-sucedida
if [ $? -eq 0 ]; then
    echo "✅ Compilação concluída com sucesso!"
else
    echo "❌ Erro durante a compilação."
    exit 1
fi
