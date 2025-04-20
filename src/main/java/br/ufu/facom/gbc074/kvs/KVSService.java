package br.ufu.facom.gbc074.kvs;

import io.grpc.stub.StreamObserver;
import org.eclipse.paho.client.mqttv3.*;
import org.json.JSONObject;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class KVSService extends KVSGrpc.KVSImplBase {

    private final MqttClient mqttClient;
    private static final String BROKER_URL = "tcp://localhost:1883";
    private static final String TOPIC = "kvs-updates";

    // Estrutura de armazenamento chave-valor com versão
    private final Map<String, TreeMap<Integer, String>> banco = new ConcurrentHashMap<>();

    public KVSService() {
        try {
            mqttClient = new MqttClient(BROKER_URL, MqttClient.generateClientId());
            mqttClient.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {}

                @Override
                public void messageArrived(String topic, MqttMessage message) {
                    handleMQTTMessage(topic, message);
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {}
            });
            mqttClient.connect();
            mqttClient.subscribe(TOPIC);
        } catch (MqttException e) {
            throw new RuntimeException("Erro ao iniciar MQTT", e);
        }
    }

    private void handleMQTTMessage(String topic, MqttMessage message) {
        String payload = new String(message.getPayload());
        JSONObject json = new JSONObject(payload);
        String chave = json.getString("chave");
        String valor = json.getString("valor");
        int versao = json.getInt("versao");

        banco.computeIfAbsent(chave, k -> new TreeMap<>()).put(versao, valor);
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

        Versao resposta = Versao.newBuilder().setVersao(versao).build();
        responseObserver.onNext(resposta);
        responseObserver.onCompleted();
    }

    @Override
    public void consulta(ChaveVersao request, StreamObserver<Tupla> responseObserver) {
        String chave = request.getChave();
        int versao = request.getVersao();

        TreeMap<Integer, String> versoes = banco.get(chave);
        Tupla.Builder tupla = Tupla.newBuilder().setChave("").setValor("").setVersao(0);

        if (versoes != null && !versoes.isEmpty()) {
            if (versao == -1) {
                versao = versoes.lastKey();
            } else if (!versoes.containsKey(versao)) {
                Optional<Map.Entry<Integer, String>> menor = versoes.floorEntry(versao) != null ?
                        Optional.of(versoes.floorEntry(versao)) : Optional.empty();
                if (menor.isPresent()) versao = menor.get().getKey();
                else versao = -1;
            }
            if (versao > 0 && versoes.containsKey(versao)) {
                tupla.setChave(chave).setValor(versoes.get(versao)).setVersao(versao);
            }
        }

        responseObserver.onNext(tupla.build());
        responseObserver.onCompleted();
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

        Versao resposta = Versao.newBuilder().setVersao(retorno).build();
        responseObserver.onNext(resposta);
        responseObserver.onCompleted();
    }

    @Override
    public void snapshot(Versao request, StreamObserver<Tupla> responseObserver) {
        int versaoLimite = request.getVersao();

        for (Map.Entry<String, TreeMap<Integer, String>> entry : banco.entrySet()) {
            String chave = entry.getKey();
            TreeMap<Integer, String> versoes = entry.getValue();

            Map.Entry<Integer, String> escolhida = (versaoLimite == 0)
                    ? versoes.lastEntry()
                    : versoes.floorEntry(versaoLimite);

            if (escolhida != null) {
                Tupla tupla = Tupla.newBuilder()
                        .setChave(chave)
                        .setValor(escolhida.getValue())
                        .setVersao(escolhida.getKey())
                        .build();
                responseObserver.onNext(tupla);
            }
        }
        responseObserver.onCompleted();
    }
}
