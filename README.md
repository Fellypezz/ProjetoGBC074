# ProjetoGBC074 – Sistema Distribuído de Armazenamento Chave-Valor (KVS)

Este projeto implementa um sistema de armazenamento chave-valor distribuído, utilizando **gRPC** para comunicação entre clientes e servidores, e **MQTT** (Mosquitto) para sincronização entre múltiplos servidores. Desenvolvido como parte do trabalho prático da disciplina GBC074.

---

## ✅ Requisitos Atendidos

| Requisito                                           | Status |
| --------------------------------------------------- | ------ |
| Comunicação cliente-servidor via gRPC               | ✅      |
| Comunicação entre servidores via MQTT               | ✅      |
| Armazenamento em memória com tabelas hash           | ✅      |
| Interface de linha de comando no servidor           | ✅      |
| Suporte a múltiplos clientes e servidores           | ✅      |
| Sincronização publish-subscribe com Mosquitto       | ✅      |
| Documentação do armazenamento e tratamento de erros | ✅      |
| Implementação completa dos métodos gRPC e streaming | ✅      |

---

## 🧠 Estrutura de Dados Utilizada

### Armazenamento de pares chave-valor com versionamento:
```java
Map<String, TreeMap<Integer, String>> store;
```

### Controle da última versão de cada chave:
```java
Map<String, AtomicInteger> versionTracker;
```

---

## 📌 Tratamento de Erros

- `consulta`: retorna `Tupla("", "", 0)` quando chave ou versão não são encontradas.
- `insere`: retorna `Versao(-1)` em caso de erro.
- `remove`: retorna `Versao(-1)` se a chave ou versão forem inválidas.
- Todos os métodos estão protegidos por blocos `try-catch`.

---

## 🖥️ Interface CLI

O servidor é executado via linha de comando:

```bash
./server.sh <porta>
```

O cliente interativo:

```bash
mvn exec:java -Dexec.mainClass="br.ufu.facom.gbc074.kvs.KVSClient"
```

Simulação automática:

```bash
mvn exec:java -Dexec.mainClass="br.ufu.facom.gbc074.kvs.KVSDemo"
```

---

## 📦 Organização dos Arquivos

- `compile.sh` – Compila o projeto via Maven
- `server.sh` – Executa o servidor KVS com porta informada
- `KVSService.java` – Implementação principal do servidor
- `KVSClient.java` – Cliente interativo
- `KVSDemo.java` – Execução automática de operações
- `kvs.proto` – Definição da API gRPC
- `pom.xml` – Configuração do Maven
- `README.md` – Este documento

---

## 🛠 Como Compilar e Executar no Linux (Ubuntu/WSL)

### 1. Instale as dependências:

```bash
sudo apt update
sudo apt install openjdk-17-jdk mosquitto maven unzip -y
```

### 2. Inicie o broker Mosquitto:

```bash
mosquitto -d
```

### 3. Compile o projeto:

```bash
./compile.sh
```

### 4. Inicie os servidores (em diferentes terminais):

```bash
./server.sh 9000
./server.sh 9001
./server.sh 9002
```

### 5. Execute os testes do professor:

```bash
cd ~/kvs-client-2024-2
./teste1-insere.sh
./teste2-consulta.sh
./teste3-remove.sh
./teste4-insere.sh
```

---

## 🧪 Funcionamento da Sincronização

- Qualquer **inserção** ou **remoção** em um servidor é publicada em `tcp://localhost:1883` no tópico `kvs/updates`.
- Os demais servidores inscritos no tópico recebem e aplicam as atualizações.
- Os dados são enviados no formato **JSON**.

---

## 💬 Observações Finais

- O sistema atende todas as especificações da proposta do trabalho.
- Implementa corretamente o versionamento, remoção, snapshot e sincronização.
- Testado em ambientes Linux e Windows com Maven e IntelliJ.
- Pode ser executado com múltiplos servidores para simular distribuição real.
- link para o youtube https://youtu.be/mO80Td8YgAk