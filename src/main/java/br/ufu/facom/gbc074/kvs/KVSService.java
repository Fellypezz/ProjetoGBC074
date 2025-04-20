package br.ufu.facom.gbc074.kvs;

import io.grpc.stub.StreamObserver;
import org.eclipse.paho.client.mqttv3.*;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

public class KVSService extends KVSGrpc.KVSImplBase {
    private final Map<String, TreeMap<Integer, String>> banco = new ConcurrentHashMap<>();
    private final Map<String, Integer> versoes = new ConcurrentHashMap<>();
    private final MqttClient mqttClient;

    public KVSService() throws MqttException {
        mqttClient = new MqttClient("tcp://localhost:1883", MqttClient.generateClientId());
        mqttClient.connect();
        mqttClient.subscribe("atualizacao", this::handleMQTTMessage);
    }

    private void handleMQTTMessage(String topic, MqttMessage message) {
        try {
            JSONObject json = new JSONObject(new String(message.getPayload(), StandardCharsets.UTF_8));
            String chave = json.getString("chave");
            int versao = json.getInt("versao");
            String valor = json.getString("valor");

            banco.putIfAbsent(chave, new TreeMap<>());
            banco.get(chave).put(versao, valor);
            versoes.put(chave, versao);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void publicarAtualizacao(JSONObject json) {
        try {
            MqttMessage mqttMessage = new MqttMessage(json.toString().getBytes(StandardCharsets.UTF_8));
            mqttClient.publish("atualizacao", mqttMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void insere(ChaveValor request, StreamObserver<Versao> responseObserver) {
        String chave = request.getChave();
        String valor = request.getValor();

        int versao = versoes.getOrDefault(chave, 0) + 1;
        versoes.put(chave, versao);

        banco.putIfAbsent(chave, new TreeMap<>());
        banco.get(chave).put(versao, valor);

        JSONObject json = new JSONObject();
        json.put("chave", chave);
        json.put("versao", versao);
        json.put("valor", valor);
        publicarAtualizacao(json);

        Versao resposta = Versao.newBuilder().setVersao(versao).build();
        responseObserver.onNext(resposta);
        responseObserver.onCompleted();
    }

    @Override
    public void consulta(ChaveVersao request, StreamObserver<Tupla> responseObserver) {
        String chave = request.getChave();
        int versao = request.getVersao();

        TreeMap<Integer, String> historico = banco.get(chave);
        if (historico != null) {
            Map.Entry<Integer, String> entrada;
            if (versao == -1) {
                entrada = historico.lastEntry();
            } else {
                entrada = historico.floorEntry(versao);
            }

            if (entrada != null) {
                Tupla resposta = Tupla.newBuilder()
                        .setChave(chave)
                        .setValor(entrada.getValue())
                        .setVersao(entrada.getKey())
                        .build();
                responseObserver.onNext(resposta);
            } else {
                responseObserver.onNext(Tupla.newBuilder().build());
            }
        } else {
            responseObserver.onNext(Tupla.newBuilder().build());
        }

        responseObserver.onCompleted();
    }

    @Override
    public void remove(ChaveVersao request, StreamObserver<Versao> responseObserver) {
        String chave = request.getChave();
        int versao = request.getVersao();

        TreeMap<Integer, String> historico = banco.get(chave);
        int respostaVersao = -1;

        if (historico != null) {
            if (versao == -1) {
                historico.clear();
                versoes.remove(chave);
                respostaVersao = 0;
            } else {
                if (historico.containsKey(versao)) {
                    historico.remove(versao);
                    respostaVersao = versao;
                    if (versao == versoes.get(chave)) {
                        versoes.put(chave, historico.isEmpty() ? 0 : historico.lastKey());
                    }
                }
            }
        }

        Versao resposta = Versao.newBuilder().setVersao(respostaVersao).build();
        responseObserver.onNext(resposta);
        responseObserver.onCompleted();
    }

    @Override
    public StreamObserver<ChaveValor> insereVarias(StreamObserver<Versao> responseObserver) {
        return new StreamObserver<>() {
            @Override
            public void onNext(ChaveValor request) {
                String chave = request.getChave();
                String valor = request.getValor();
                int versao = versoes.getOrDefault(chave, 0) + 1;
                versoes.put(chave, versao);

                banco.putIfAbsent(chave, new TreeMap<>());
                banco.get(chave).put(versao, valor);

                JSONObject json = new JSONObject();
                json.put("chave", chave);
                json.put("versao", versao);
                json.put("valor", valor);
                publicarAtualizacao(json);

                responseObserver.onNext(Versao.newBuilder().setVersao(versao).build());
            }

            @Override
            public void onError(Throwable t) {
                responseObserver.onError(t);
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
                String chaveReq = request.getChave();
                int versaoReq = request.getVersao();

                TreeMap<Integer, String> historico = banco.get(chaveReq);
                if (historico != null) {
                    Map.Entry<Integer, String> entrada;
                    if (versaoReq == -1) {
                        entrada = historico.lastEntry();
                    } else {
                        entrada = historico.floorEntry(versaoReq);
                    }

                    if (entrada != null) {
                        Tupla resposta = Tupla.newBuilder()
                                .setChave(chaveReq)
                                .setValor(entrada.getValue())
                                .setVersao(entrada.getKey())
                                .build();
                        responseObserver.onNext(resposta);
                    } else {
                        responseObserver.onNext(Tupla.newBuilder().build());
                    }
                } else {
                    responseObserver.onNext(Tupla.newBuilder().build());
                }
            }

            @Override
            public void onError(Throwable t) {
                responseObserver.onError(t);
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
                String chave = request.getChave();
                int versao = request.getVersao();

                TreeMap<Integer, String> historico = banco.get(chave);
                int respostaVersao = -1;

                if (historico != null) {
                    if (versao == -1) {
                        historico.clear();
                        versoes.remove(chave);
                        respostaVersao = 0;
                    } else {
                        if (historico.containsKey(versao)) {
                            historico.remove(versao);
                            respostaVersao = versao;
                            if (versao == versoes.get(chave)) {
                                versoes.put(chave, historico.isEmpty() ? 0 : historico.lastKey());
                            }
                        }
                    }
                }

                Versao resposta = Versao.newBuilder().setVersao(respostaVersao).build();
                responseObserver.onNext(resposta);
            }

            @Override
            public void onError(Throwable t) {
                responseObserver.onError(t);
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

        for (Map.Entry<String, TreeMap<Integer, String>> entrada : banco.entrySet()) {
            String chave = entrada.getKey();
            TreeMap<Integer, String> historico = entrada.getValue();

            Map.Entry<Integer, String> tupla = (versaoLimite == 0)
                    ? historico.lastEntry()
                    : historico.floorEntry(versaoLimite);

            if (tupla != null) {
                Tupla resposta = Tupla.newBuilder()
                        .setChave(chave)
                        .setValor(tupla.getValue())
                        .setVersao(tupla.getKey())
                        .build();
                responseObserver.onNext(resposta);
            }
        }

        responseObserver.onCompleted();
    }
}
