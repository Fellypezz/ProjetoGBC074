package br.ufu.facom.gbc074.kvs;

import io.grpc.stub.StreamObserver;
import org.eclipse.paho.client.mqttv3.*;
import org.json.JSONObject;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class KVSService extends KVSGrpc.KVSImplBase {

    private final Map<String, TreeMap<Integer, String>> store = new ConcurrentHashMap<>();
    private final Map<String, AtomicInteger> versionTracker = new ConcurrentHashMap<>();
    private final MqttClient mqttClient;

    public KVSService() throws MqttException {
        mqttClient = new MqttClient("tcp://localhost:1883", MqttClient.generateClientId());
        mqttClient.connect();
        mqttClient.subscribe("kvs/updates", this::handleMQTTMessage);
    }

    private void handleMQTTMessage(String topic, MqttMessage message) {
        try {
            String payload = new String(message.getPayload());
            JSONObject json = new JSONObject(payload);

            String tipo = json.getString("tipo");
            String chave = json.getString("chave");

            switch (tipo) {
                case "INSERE":
                    int versao = json.getInt("versao");
                    String valor = json.getString("valor");

                    AtomicInteger currentVersion = versionTracker.getOrDefault(chave, new AtomicInteger(0));
                    if (versao > currentVersion.get()) {
                        store.putIfAbsent(chave, new TreeMap<>());
                        store.get(chave).put(versao, valor);
                        versionTracker.putIfAbsent(chave, new AtomicInteger(versao));
                        versionTracker.get(chave).updateAndGet(v -> Math.max(v, versao));
                    }
                    break;

                case "REMOVE":
                    int vRemover = json.getInt("versao");
                    if (store.containsKey(chave)) {
                        store.get(chave).remove(vRemover);
                    }
                    break;

                case "REMOVE_TODOS":
                    if (store.containsKey(chave)) {
                        store.get(chave).clear();
                    }
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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

            if (chave == null || valor == null) {
                throw new IllegalArgumentException("Chave e valor não podem ser nulos");
            }

            store.putIfAbsent(chave, new TreeMap<>());
            versionTracker.putIfAbsent(chave, new AtomicInteger(0));

            int novaVersao = versionTracker.get(chave).incrementAndGet();
            store.get(chave).put(novaVersao, valor);

            JSONObject json = new JSONObject();
            json.put("tipo", "INSERE");
            json.put("chave", chave);
            json.put("valor", valor);
            json.put("versao", novaVersao);
            publicarAtualizacao(json);

            responseObserver.onNext(Versao.newBuilder().setVersao(novaVersao).build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onNext(Versao.newBuilder().setVersao(-1).build());
            responseObserver.onCompleted();
        }
    }

    @Override
    public void consulta(ChaveVersao request, StreamObserver<Tupla> responseObserver) {
        try {
            String chave = request.getChave();
            int versao = request.hasVersao() ? request.getVersao() : Integer.MAX_VALUE;

            if (!store.containsKey(chave)) {
                responseObserver.onNext(Tupla.newBuilder()
                        .setChave("")
                        .setValor("")
                        .setVersao(0)
                        .build());
                responseObserver.onCompleted();
                return;
            }

            TreeMap<Integer, String> versoes = store.get(chave);
            Map.Entry<Integer, String> entry = versoes.floorEntry(versao);

            if (entry == null) {
                responseObserver.onNext(Tupla.newBuilder()
                        .setChave("")
                        .setValor("")
                        .setVersao(0)
                        .build());
            } else {
                responseObserver.onNext(Tupla.newBuilder()
                        .setChave(chave)
                        .setValor(entry.getValue())
                        .setVersao(entry.getKey())
                        .build());
            }
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onNext(Tupla.newBuilder()
                    .setChave("")
                    .setValor("")
                    .setVersao(0)
                    .build());
            responseObserver.onCompleted();
        }
    }

    @Override
    public void remove(ChaveVersao request, StreamObserver<Versao> responseObserver) {
        try {
            String chave = request.getChave();

            if (!store.containsKey(chave)) {
                responseObserver.onNext(Versao.newBuilder().setVersao(-1).build());
                responseObserver.onCompleted();
                return;
            }

            TreeMap<Integer, String> versoes = store.get(chave);
            JSONObject json = new JSONObject();
            json.put("chave", chave);

            if (request.hasVersao()) {
                int versao = request.getVersao();
                if (versoes.containsKey(versao)) {
                    versoes.remove(versao);
                    json.put("tipo", "REMOVE");
                    json.put("versao", versao);
                    publicarAtualizacao(json);
                    responseObserver.onNext(Versao.newBuilder().setVersao(versao).build());
                } else {
                    responseObserver.onNext(Versao.newBuilder().setVersao(-1).build());
                }
            } else {
                int ultimaVersao = versionTracker.getOrDefault(chave, new AtomicInteger(0)).get();
                versoes.clear();
                json.put("tipo", "REMOVE_TODOS");
                publicarAtualizacao(json);
                responseObserver.onNext(Versao.newBuilder().setVersao(ultimaVersao).build());
            }
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onNext(Versao.newBuilder().setVersao(-1).build());
            responseObserver.onCompleted();
        }
    }

    @Override
    public StreamObserver<ChaveValor> insereVarias(StreamObserver<Versao> responseObserver) {
        return new StreamObserver<>() {
            @Override
            public void onNext(ChaveValor request) {
                insere(request, new StreamObserver<>() {
                    @Override
                    public void onNext(Versao versao) {
                        responseObserver.onNext(versao);
                    }

                    @Override
                    public void onError(Throwable throwable) {}

                    @Override
                    public void onCompleted() {}
                });
            }

            @Override
            public void onError(Throwable throwable) {
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
        return new StreamObserver<>() {
            @Override
            public void onNext(ChaveVersao request) {
                consulta(request, new StreamObserver<>() {
                    @Override
                    public void onNext(Tupla tupla) {
                        responseObserver.onNext(tupla);
                    }

                    @Override
                    public void onError(Throwable throwable) {}

                    @Override
                    public void onCompleted() {}
                });
            }

            @Override
            public void onError(Throwable throwable) {
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
        return new StreamObserver<>() {
            @Override
            public void onNext(ChaveVersao request) {
                remove(request, new StreamObserver<>() {
                    @Override
                    public void onNext(Versao versao) {
                        responseObserver.onNext(versao);
                    }

                    @Override
                    public void onError(Throwable throwable) {}

                    @Override
                    public void onCompleted() {}
                });
            }

            @Override
            public void onError(Throwable throwable) {
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
        int versao = request.getVersao();

        for (Map.Entry<String, TreeMap<Integer, String>> entry : store.entrySet()) {
            String chave = entry.getKey();
            TreeMap<Integer, String> versoes = entry.getValue();

            Map.Entry<Integer, String> encontrada = versao <= 0
                    ? versoes.lastEntry()
                    : versoes.floorEntry(versao);

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
