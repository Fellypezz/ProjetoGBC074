
# Projeto: Sistema Distribuído de Armazenamento Chave-Valor (KVS)

Este projeto implementa um sistema de armazenamento chave-valor distribuído com comunicação entre clientes e servidores via **gRPC**, e sincronização entre servidores via **MQTT** (Mosquitto), conforme as especificações do trabalho prático.

---

## 🔧 Requisitos Atendidos

### ✅ gRPC entre cliente e servidor
> A comunicação entre cliente e servidor utiliza `gRPC` com definição via `.proto`.

### ✅ MQTT entre servidores
> Os servidores utilizam a biblioteca Eclipse Paho para publicar e assinar mensagens MQTT em um broker local (Mosquitto).

### ✅ Tabelas hash locais em memória
> As chaves e versões são armazenadas em:
```java
Map<String, TreeMap<Integer, String>> store;
Map<String, AtomicInteger> versionTracker;
```
> Tudo mantido em memória.

### ✅ Interface CLI no servidor
> O servidor é iniciado por linha de comando com:
```bash
./server.sh <porta>
```

### ✅ Tratamento de Erros nas APIs
> - `consulta`: retorna `"", "", 0` quando chave ou versão não encontradas
> - `insere`: retorna `-1` em caso de exceções
> - `remove`: retorna `-1` para chave/versão inválidas
> - Todos os métodos estão protegidos com `try-catch`

### ✅ Documentação do esquema de dados
> O funcionamento e estrutura das tabelas é documentado neste README.

### ✅ Execução de múltiplos clientes e servidores
> É possível executar várias instâncias simultaneamente com portas diferentes.

### ✅ Publish-Subscribe entre servidores
> Toda inserção/remoção é propagada via MQTT para o tópico `kvs/updates`.

### ✅ Uso do broker Mosquitto em localhost:1883
> Os servidores se conectam em `tcp://localhost:1883` com configuração padrão.

---

## 🛠 Instruções para Compilar e Executar (Linux)

### 1. Instalar dependências:
```bash
sudo apt update
sudo apt install openjdk-17-jdk mosquitto unzip
```

### 2. Dar permissão aos scripts:
```bash
chmod +x compile.sh server.sh
```

### 3. Compilar:
```bash
./compile.sh
```

### 4. Executar o servidor:
```bash
./server.sh 50051
```

### 5. Executar cliente:
```bash
java -cp "bin:lib/*" br.ufu.facom.gbc074.kvs.KVSClient
```

### 6. Ou executar a demo:
```bash
java -cp "bin:lib/*" br.ufu.facom.gbc074.kvs.KVSDemo
```

---

## 📂 Estrutura dos Dados

### Armazenamento de pares chave-valor:
```java
Map<String, TreeMap<Integer, String>> store;
```
- Cada `chave` tem um `TreeMap` com versões ordenadas.
- A primeira versão é `1` e é incrementada a cada atualização da chave.

### Controle de versões:
```java
Map<String, AtomicInteger> versionTracker;
```
- Mantém o número da última versão usada por cada chave.

---

## ⚠️ Tratamento de Erros

- `Chave não encontrada`: retorna `Tupla("", "", 0)`
- `Versão inexistente`: mesma resposta acima
- `Falha ao inserir`: retorna `Versao(-1)`
- `Falha ao remover`: retorna `Versao(-1)`

---

## 📦 Arquivos Incluídos

- `compile.sh`: script para compilar tudo para `bin/`
- `server.sh`: executa o servidor com a porta passada como argumento
- `README.md`: este arquivo
- `KVSService.java`: lógica principal do servidor
- `KVSClient.java`: cliente interativo por terminal
- `KVSDemo.java`: simulação completa com prints automáticos

---

## 📌 Observações Finais

- O sistema está completamente funcional para uso local e com vários servidores interligados via MQTT.
- O comportamento de versões, remoção e snapshot está conforme as especificações.
- Todos os testes foram feitos no IntelliJ e no Ubuntu 22.04.

