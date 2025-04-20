package br.ufu.facom.gbc074.kvs;

import io.grpc.stub.StreamObserver;
import org.eclipse.paho.client.mqttv3.*;
import org.json.JSONObject;

import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class KVSService extends KVSGrpc.KVSImplBase {

    // Armazenamento principal: chave -> (mapa ordenado de versao -> valor)
    private final Map<String, TreeMap<Integer, String>> store = new ConcurrentHashMap<>();
    // Controle de versão atual por chave
    private final Map<String, AtomicInteger> versionTracker = new ConcurrentHashMap<>();
    // Cliente MQTT para publicar/assinar atualizações
    private final MqttClient mqttClient;

    public KVSService() throws MqttException {
        // Conecta ao broker MQTT local
        mqttClient = new MqttClient("tcp://localhost:1883", MqttClient.generateClientId());
        mqttClient.connect();
        // Inscreve-se no tópico de atualizações para sincronização distribuída
        mqttClient.subscribe("kvs/updates", this::handleMQTTMessage);
    }

    /**
     * Callback chamado ao receber uma mensagem MQTT no tópico "kvs/updates".
     * Aplica a operação recebida (INSERE, REMOVE ou REMOVE_TODOS) ao estado local.
     */
    private void handleMQTTMessage(String topic, MqttMessage message) {
        try {
            String payload = new String(message.getPayload());
            JSONObject json = new JSONObject(payload);

            String tipo = json.getString("tipo");
            String chave = json.getString("chave");

            switch (tipo) {
                case "INSERE": {
                    int versao = json.getInt("versao");
                    String valor = json.getString("valor");
                    // Aplica somente se for uma versão nova (maior que a versão atual conhecida)
                    AtomicInteger currentVersion = versionTracker.getOrDefault(chave, new AtomicInteger(0));
                    if (versao > currentVersion.get()) {
                        store.putIfAbsent(chave, new TreeMap<>());
                        store.get(chave).put(versao, valor);
                        // Atualiza o rastreador de versão para no mínimo essa versão recebida
                        versionTracker.putIfAbsent(chave, new AtomicInteger(versao));
                        versionTracker.get(chave).updateAndGet(v -> Math.max(v, versao));
                    }
                    break;
                }
                case "REMOVE": {
                    int versaoRemover = json.getInt("versao");
                    if (store.containsKey(chave)) {
                        store.get(chave).remove(versaoRemover);
                    }
                    break;
                }
                case "REMOVE_TODOS": {
                    if (store.containsKey(chave)) {
                        store.get(chave).clear();
                    }
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** Publica uma mensagem de atualização no tópico MQTT compartilhado. */
    private void publicarAtualizacao(JSONObject json) {
        try {
            mqttClient.publish("kvs/updates", new MqttMessage(json.toString().getBytes()));
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void insere(ChaveValor request, StreamObserver<Versao> responseObserver) {
        try {
            String chave = request.getChave();
            String valor = request.getValor();

            // Validação básica de entrada
            if (chave == null || valor == null) {
                throw new IllegalArgumentException("Chave e valor não podem ser nulos");
            }

            // Garante que as estruturas estejam inicializadas para a chave
            store.putIfAbsent(chave, new TreeMap<>());
            versionTracker.putIfAbsent(chave, new AtomicInteger(0));

            // Gera nova versão atômica
            int novaVersao = versionTracker.get(chave).incrementAndGet();
            store.get(chave).put(novaVersao, valor);

            // Publica a inserção via MQTT para os outros servidores
            JSONObject json = new JSONObject();
            json.put("tipo", "INSERE");
            json.put("chave", chave);
            json.put("valor", valor);
            json.put("versao", novaVersao);
            publicarAtualizacao(json);

            // Retorna a versão gerada
            responseObserver.onNext(Versao.newBuilder().setVersao(novaVersao).build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            // Em caso de erro, retorna versão -1 indicando falha
            responseObserver.onNext(Versao.newBuilder().setVersao(-1).build());
            responseObserver.onCompleted();
        }
    }

    @Override
    public void consulta(ChaveVersao request, StreamObserver<Tupla> responseObserver) {
        try {
            String chave = request.getChave();
            // Se nenhuma versão for especificada, ou se for <= 0, usamos como sinal para buscar a última versão
            int versaoSolicitada = request.hasVersao() ? request.getVersao() : 0;

            if (!store.containsKey(chave) || store.get(chave).isEmpty()) {
                // Chave inexistente ou sem valores -> retorna tupla "vazia" com versao -1
                responseObserver.onNext(Tupla.newBuilder()
                        .setChave(chave)
                        .setValor("")
                        .setVersao(-1)
                        .build());
                responseObserver.onCompleted();
                return;
            }

            TreeMap<Integer, String> versoes = store.get(chave);
            Map.Entry<Integer, String> entrada;
            if (versaoSolicitada <= 0) {
                // Versão <= 0 (ex.: -1 ou 0) -> pega a última versão disponível
                entrada = versoes.lastEntry();
            } else {
                // Versão positiva -> pega a maior versão <= solicitada (floorEntry)
                entrada = versoes.floorEntry(versaoSolicitada);
            }

            if (entrada == null) {
                // Nenhuma versão <= versaoSolicitada
                responseObserver.onNext(Tupla.newBuilder()
                        .setChave(chave)
                        .setValor("")
                        .setVersao(-1)
                        .build());
            } else {
                responseObserver.onNext(Tupla.newBuilder()
                        .setChave(chave)
                        .setValor(entrada.getValue())
                        .setVersao(entrada.getKey())
                        .build());
            }
            responseObserver.onCompleted();
        } catch (Exception e) {
            // Em caso de erro, retorna tupla vazia com versão -1
            responseObserver.onNext(Tupla.newBuilder()
                    .setChave("")
                    .setValor("")
                    .setVersao(-1)
                    .build());
            responseObserver.onCompleted();
        }
    }

    @Override
    public void remove(ChaveVersao request, StreamObserver<Versao> responseObserver) {
        try {
            String chave = request.getChave();

            if (!store.containsKey(chave) || store.get(chave).isEmpty()) {
                // Chave não existe ou não há nada a remover
                responseObserver.onNext(Versao.newBuilder().setVersao(-1).build());
                responseObserver.onCompleted();
                return;
            }

            TreeMap<Integer, String> versoes = store.get(chave);
            JSONObject json = new JSONObject();
            json.put("chave", chave);

            if (request.hasVersao()) {
                // Remoção de uma versão específica
                int versao = request.getVersao();
                if (versoes.containsKey(versao)) {
                    versoes.remove(versao);
                    json.put("tipo", "REMOVE");
                    json.put("versao", versao);
                    publicarAtualizacao(json);
                    responseObserver.onNext(Versao.newBuilder().setVersao(versao).build());
                } else {
                    // Versão solicitada não existe
                    responseObserver.onNext(Versao.newBuilder().setVersao(-1).build());
                }
            } else {
                // Remoção de todas as versões da chave
                versoes.clear();
                json.put("tipo", "REMOVE_TODOS");
                publicarAtualizacao(json);
                // Retorna 0 indicando que a chave ficou sem versões (todas removidas)
                responseObserver.onNext(Versao.newBuilder().setVersao(0).build());
            }
            responseObserver.onCompleted();
        } catch (Exception e) {
            // Em caso de erro, retorna -1
            responseObserver.onNext(Versao.newBuilder().setVersao(-1).build());
            responseObserver.onCompleted();
        }
    }

    @Override
    public StreamObserver<ChaveValor> insereVarias(StreamObserver<Versao> responseObserver) {
        // Stream de entrada (várias inserções) e saída (versões correspondentes)
        return new StreamObserver<ChaveValor>() {
            @Override
            public void onNext(ChaveValor request) {
                // Para cada item recebido, reutiliza o método insere e envia o resultado
                insere(request, new StreamObserver<Versao>() {
                    @Override
                    public void onNext(Versao versao) {
                        responseObserver.onNext(versao);
                    }
                    @Override
                    public void onError(Throwable t) { /* Ignorado, tratado no outer observer */ }
                    @Override
                    public void onCompleted() { /* Não faz nada aqui */ }
                });
            }
            @Override
            public void onError(Throwable t) {
                responseObserver.onCompleted();
            }
            @Override
            public void onCompleted() {
                responseObserver.onCompleted();
            }
        };
    }

    @Override
    public StreamObserver<ChaveVersao> consultaVarias(StreamObserver<Tupla> responseObserver) {
        // Stream de entrada (várias consultas) e saída (resultados correspondentes)
        return new StreamObserver<ChaveVersao>() {
            @Override
            public void onNext(ChaveVersao request) {
                consulta(request, new StreamObserver<Tupla>() {
                    @Override
                    public void onNext(Tupla tupla) {
                        responseObserver.onNext(tupla);
                    }
                    @Override
                    public void onError(Throwable t) { /* Ignorado */ }
                    @Override
                    public void onCompleted() { /* Ignorado */ }
                });
            }
            @Override
            public void onError(Throwable t) {
                responseObserver.onCompleted();
            }
            @Override
            public void onCompleted() {
                responseObserver.onCompleted();
            }
        };
    }

    @Override
    public StreamObserver<ChaveVersao> removeVarias(StreamObserver<Versao> responseObserver) {
        // Stream de entrada (várias remoções) e saída (versões removidas ou -1)
        return new StreamObserver<ChaveVersao>() {
            @Override
            public void onNext(ChaveVersao request) {
                remove(request, new StreamObserver<Versao>() {
                    @Override
                    public void onNext(Versao versao) {
                        responseObserver.onNext(versao);
                    }
                    @Override
                    public void onError(Throwable t) { /* Ignorado */ }
                    @Override
                    public void onCompleted() { /* Ignorado */ }
                });
            }
            @Override
            public void onError(Throwable t) {
                responseObserver.onCompleted();
            }
            @Override
            public void onCompleted() {
                responseObserver.onCompleted();
            }
        };
    }

    @Override
    public void snapshot(Versao request, StreamObserver<Tupla> responseObserver) {
        int versaoLimite = request.getVersao();
        // Para cada chave no armazenamento, retorna a tupla (chave, valor, versao) apropriada
        for (Map.Entry<String, TreeMap<Integer, String>> entry : store.entrySet()) {
            String chave = entry.getKey();
            TreeMap<Integer, String> versoes = entry.getValue();

            // Seleciona a maior versão <= versaoLimite; se versaoLimite <= 0, pega a última versão disponível
            Map.Entry<Integer, String> encontrada = (versaoLimite <= 0)
                    ? versoes.lastEntry()
                    : versoes.floorEntry(versaoLimite);

            if (encontrada != null) {
                responseObserver.onNext(
                        Tupla.newBuilder()
                                .setChave(chave)
                                .setValor(encontrada.getValue())
                                .setVersao(encontrada.getKey())
                                .build()
                );
            }
        }
        responseObserver.onCompleted();
    }
}
