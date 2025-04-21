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

    public KVSService(int porta) throws MqttException {
        mqttClient = new MqttClient(BROKER_URL, MqttClient.generateClientId() + "-" + porta);
        mqttClient.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {}

            @Override
            public void messageArrived(String topic, MqttMessage message) {
                String payload = new String(message.getPayload());
                JSONObject json = new JSONObject(payload);
                String chave = json.getString("chave");
                String valor = json.getString("valor");
                int versao = json.getInt("versao");

                banco.computeIfAbsent(chave, k -> new TreeMap<>()).put(versao, valor);
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {}
        });
        mqttClient.connect();
        mqttClient.subscribe(TOPIC);
    }

    private void publicarAtualizacao(String chave, String valor, int versao) {
        JSONObject json = new JSONObject();
        json.put("chave", chave);
        json.put("valor", valor);
        json.put("versao", versao);
        try {
            mqttClient.publish(TOPIC, new MqttMessage(json.toString().getBytes()));
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    private int getProximaVersao(String chave) {
        TreeMap<Integer, String> versoes = banco.get(chave);
        return (versoes == null || versoes.isEmpty()) ? 1 : versoes.lastKey() + 1;
    }

    @Override
    public void insere(ChaveValor request, StreamObserver<Versao> responseObserver) {
        String chave = request.getChave();
        String valor = request.getValor();
        int versao = getProximaVersao(chave);
        banco.computeIfAbsent(chave, k -> new TreeMap<>()).put(versao, valor);
        publicarAtualizacao(chave, valor, versao);
        responseObserver.onNext(Versao.newBuilder().setVersao(versao).build());
        responseObserver.onCompleted();
    }

    @Override
    public StreamObserver<ChaveValor> insereVarias(StreamObserver<Versao> responseObserver) {
        return new StreamObserver<>() {
            @Override
            public void onNext(ChaveValor request) {
                insere(request, responseObserver);
            }

            @Override
            public void onError(Throwable t) {}

            @Override
            public void onCompleted() {
                responseObserver.onCompleted();
            }
        };
    }

    @Override
    public void consulta(ChaveVersao request, StreamObserver<Tupla> responseObserver) {
        String chave = request.getChave();
        int versao = request.getVersao();
        TreeMap<Integer, String> versoes = banco.get(chave);
        Tupla.Builder tupla = Tupla.newBuilder().setChave("").setValor("").setVersao(-1);

        if (versoes != null && !versoes.isEmpty()) {
            Integer chaveVersao = versao == -1 ? versoes.lastKey() : versoes.floorKey(versao);
            if (chaveVersao != null) {
                tupla.setChave(chave).setValor(versoes.get(chaveVersao)).setVersao(chaveVersao);
            }
        }

        responseObserver.onNext(tupla.build());
        responseObserver.onCompleted();
    }

    @Override
    public StreamObserver<ChaveVersao> consultaVarias(StreamObserver<Tupla> responseObserver) {
        return new StreamObserver<>() {
            @Override
            public void onNext(ChaveVersao request) {
                consulta(request, responseObserver);
            }

            @Override
            public void onError(Throwable t) {}

            @Override
            public void onCompleted() {
                responseObserver.onCompleted();
            }
        };
    }

    @Override
    public void remove(ChaveVersao request, StreamObserver<Versao> responseObserver) {
        String chave = request.getChave();
        int versao = request.getVersao();
        TreeMap<Integer, String> versoes = banco.get(chave);
        int retorno = -1;
        if (versoes != null && !versoes.isEmpty()) {
            if (versao == -1) {
                retorno = versoes.lastKey();
                versoes.remove(retorno);
            } else if (versoes.containsKey(versao)) {
                versoes.remove(versao);
                retorno = versao;
            }
        }
        responseObserver.onNext(Versao.newBuilder().setVersao(retorno).build());
        responseObserver.onCompleted();
    }

    @Override
    public StreamObserver<ChaveVersao> removeVarias(StreamObserver<Versao> responseObserver) {
        return new StreamObserver<>() {
            @Override
            public void onNext(ChaveVersao request) {
                remove(request, responseObserver);
            }

            @Override
            public void onError(Throwable t) {}

            @Override
            public void onCompleted() {
                responseObserver.onCompleted();
            }
        };
    }

    @Override
    public void snapshot(Versao request, StreamObserver<Tupla> responseObserver) {
        int versaoLimite = request.getVersao();
        for (Map.Entry<String, TreeMap<Integer, String>> entry : banco.entrySet()) {
            String chave = entry.getKey();
            TreeMap<Integer, String> versoes = entry.getValue();
            Map.Entry<Integer, String> selecionada =
                    versaoLimite == 0 ? versoes.lastEntry() : versoes.floorEntry(versaoLimite);

            if (selecionada != null) {
                Tupla tupla = Tupla.newBuilder()
                        .setChave(chave)
                        .setValor(selecionada.getValue())
                        .setVersao(selecionada.getKey())
                        .build();
                responseObserver.onNext(tupla);
            }
        }
        responseObserver.onCompleted();
    }
}
