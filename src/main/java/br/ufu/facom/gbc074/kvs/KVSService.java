package br.ufu.facom.gbc074.kvs;

import io.grpc.stub.StreamObserver;
import org.eclipse.paho.client.mqttv3.*;
import org.json.JSONObject;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class KVSService extends KVSGrpc.KVSImplBase {
    private static final String BROKER_URL = "tcp://localhost:1883";
    private static final String TOPIC = "kvs-updates";
    private final MqttClient mqttClient;
    private final Map<String, TreeMap<Integer, String>> banco = new ConcurrentHashMap<>();

    public KVSService() throws MqttException {
        mqttClient = new MqttClient(BROKER_URL, MqttClient.generateClientId());
        mqttClient.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {}

            @Override
            public void messageArrived(String topic, MqttMessage message) {
                JSONObject json = new JSONObject(new String(message.getPayload()));
                String chave = json.getString("chave");
                int versao = json.getInt("versao");
                String valor = json.optString("valor", null);
                boolean remover = json.optBoolean("remover", false);

                banco.putIfAbsent(chave, new TreeMap<>());
                TreeMap<Integer, String> historico = banco.get(chave);

                if (remover) {
                    historico.remove(versao);
                } else {
                    if (!historico.containsKey(versao)) {
                        historico.put(versao, valor);
                    }
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {}
        });

        mqttClient.connect();
        mqttClient.subscribe(TOPIC);
    }

    private void publicarMQTT(String chave, int versao, String valor, boolean remover) {
        try {
            JSONObject json = new JSONObject();
            json.put("chave", chave);
            json.put("versao", versao);
            json.put("remover", remover);
            if (!remover) json.put("valor", valor);
            mqttClient.publish(TOPIC, new MqttMessage(json.toString().getBytes()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void insere(ChaveValor req, StreamObserver<Versao> responseObserver) {
        String chave = req.getChave();
        String valor = req.getValor();
        banco.putIfAbsent(chave, new TreeMap<>());
        TreeMap<Integer, String> historico = banco.get(chave);
        int novaVersao = historico.isEmpty() ? 1 : historico.lastKey() + 1;
        historico.put(novaVersao, valor);
        publicarMQTT(chave, novaVersao, valor, false);
        responseObserver.onNext(Versao.newBuilder().setVersao(novaVersao).build());
        responseObserver.onCompleted();
    }

    @Override
    public void consulta(ChaveVersao req, StreamObserver<Tupla> responseObserver) {
        String chave = req.getChave();
        int versao = req.hasVersao() ? req.getVersao() : -1;
        Tupla.Builder resposta = Tupla.newBuilder().setChave("").setValor("").setVersao(-1);
        if (banco.containsKey(chave)) {
            TreeMap<Integer, String> historico = banco.get(chave);
            Map.Entry<Integer, String> entrada = versao < 0 ? historico.lastEntry() : historico.floorEntry(versao);
            if (entrada != null) {
                resposta.setChave(chave).setValor(entrada.getValue()).setVersao(entrada.getKey());
            }
        }
        responseObserver.onNext(resposta.build());
        responseObserver.onCompleted();
    }

    @Override
    public void remove(ChaveVersao req, StreamObserver<Versao> responseObserver) {
        String chave = req.getChave();
        int versao = req.hasVersao() ? req.getVersao() : -1;
        int removido = -1;
        if (banco.containsKey(chave)) {
            TreeMap<Integer, String> historico = banco.get(chave);
            Map.Entry<Integer, String> entrada = versao < 0 ? historico.lastEntry() : historico.floorEntry(versao);
            if (entrada != null) {
                historico.remove(entrada.getKey());
                publicarMQTT(chave, entrada.getKey(), null, true);
                removido = entrada.getKey();
            }
        }
        responseObserver.onNext(Versao.newBuilder().setVersao(removido).build());
        responseObserver.onCompleted();
    }

    @Override
    public void snapshot(Versao req, StreamObserver<Tupla> responseObserver) {
        int verLimite = req.getVersao();
        for (Map.Entry<String, TreeMap<Integer, String>> entry : banco.entrySet()) {
            String chave = entry.getKey();
            TreeMap<Integer, String> historico = entry.getValue();
            Map.Entry<Integer, String> versaoValida = verLimite <= 0 ? historico.lastEntry() : historico.floorEntry(verLimite);
            if (versaoValida != null) {
                Tupla tupla = Tupla.newBuilder()
                        .setChave(chave)
                        .setValor(versaoValida.getValue())
                        .setVersao(versaoValida.getKey())
                        .build();
                responseObserver.onNext(tupla);
            }
        }
        responseObserver.onCompleted();
    }

    @Override
    public StreamObserver<ChaveValor> insereVarias(StreamObserver<Versao> responseObserver) {
        return new StreamObserver<>() {
            int ultimaVersao = -1;

            @Override
            public void onNext(ChaveValor req) {
                String chave = req.getChave();
                String valor = req.getValor();
                banco.putIfAbsent(chave, new TreeMap<>());
                TreeMap<Integer, String> historico = banco.get(chave);
                int novaVersao = historico.isEmpty() ? 1 : historico.lastKey() + 1;
                historico.put(novaVersao, valor);
                publicarMQTT(chave, novaVersao, valor, false);
                ultimaVersao = novaVersao;
                responseObserver.onNext(Versao.newBuilder().setVersao(novaVersao).build());
            }

            @Override
            public void onError(Throwable t) {
                t.printStackTrace();
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
            public void onNext(ChaveVersao req) {
                String chave = req.getChave();
                int versao = req.hasVersao() ? req.getVersao() : -1;
                if (banco.containsKey(chave)) {
                    TreeMap<Integer, String> historico = banco.get(chave);
                    Map.Entry<Integer, String> entrada = versao < 0 ? historico.lastEntry() : historico.floorEntry(versao);
                    if (entrada != null) {
                        Tupla tupla = Tupla.newBuilder()
                                .setChave(chave)
                                .setValor(entrada.getValue())
                                .setVersao(entrada.getKey())
                                .build();
                        responseObserver.onNext(tupla);
                    }
                }
            }

            @Override
            public void onError(Throwable t) {
                t.printStackTrace();
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
            public void onNext(ChaveVersao req) {
                String chave = req.getChave();
                int versao = req.hasVersao() ? req.getVersao() : -1;
                if (banco.containsKey(chave)) {
                    TreeMap<Integer, String> historico = banco.get(chave);
                    Map.Entry<Integer, String> entrada = versao < 0 ? historico.lastEntry() : historico.floorEntry(versao);
                    if (entrada != null) {
                        historico.remove(entrada.getKey());
                        publicarMQTT(chave, entrada.getKey(), null, true);
                        responseObserver.onNext(Versao.newBuilder().setVersao(entrada.getKey()).build());
                    }
                }
            }

            @Override
            public void onError(Throwable t) {
                t.printStackTrace();
            }

            @Override
            public void onCompleted() {
                responseObserver.onCompleted();
            }
        };
    }
}