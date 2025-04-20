
package br.ufu.facom.gbc074.kvs;

import io.grpc.stub.StreamObserver;
import org.eclipse.paho.client.mqttv3.*;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class KVSService extends KVSGrpc.KVSImplBase {
    private final MqttClient mqttClient;
    private final Map<String, Map<Integer, String>> banco = new ConcurrentHashMap<>();

    public KVSService() throws MqttException {
        mqttClient = new MqttClient("tcp://localhost:1883", MqttClient.generateClientId());
        mqttClient.setCallback(new MqttCallback() {
            public void connectionLost(Throwable cause) {}
            public void messageArrived(String topic, MqttMessage message) {
                handleMQTTMessage(topic, message);
            }
            public void deliveryComplete(IMqttDeliveryToken token) {}
        });
        mqttClient.connect();
        mqttClient.subscribe("kvs/updates");
    }

    private void handleMQTTMessage(String topic, MqttMessage message) {
        // processa mensagens MQTT (omitido para simplicidade)
    }

    private void publicarAtualizacao(JSONObject json) {
        try {
            MqttMessage msg = new MqttMessage(json.toString().getBytes());
            mqttClient.publish("kvs/updates", msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void insere(ChaveValor request, StreamObserver<Versao> responseObserver) {
        String key = request.getChave();
        String value = request.getValor();

        banco.putIfAbsent(key, new HashMap<>());
        int novaVersao = banco.get(key).size() + 1;
        banco.get(key).put(novaVersao, value);

        JSONObject json = new JSONObject();
        json.put("op", "insere");
        json.put("chave", key);
        json.put("valor", value);
        json.put("versao", novaVersao);

        publicarAtualizacao(json);

        Versao versao = Versao.newBuilder().setVersao(novaVersao).build();
        responseObserver.onNext(versao);
        responseObserver.onCompleted();
    }

    @Override
    public void consulta(ChaveVersao request, StreamObserver<Tupla> responseObserver) {
        String key = request.getChave();
        int ver = request.getVersao();

        Tupla.Builder resposta = Tupla.newBuilder();

        if (banco.containsKey(key)) {
            Map<Integer, String> versoes = banco.get(key);
            if (ver <= 0) {
                ver = versoes.keySet().stream().mapToInt(i -> i).max().orElse(0);
            }
            String valor = versoes.getOrDefault(ver, "");
            if (!valor.isEmpty()) {
                resposta.setChave(key).setValor(valor).setVersao(ver);
            }
        }

        responseObserver.onNext(resposta.build());
        responseObserver.onCompleted();
    }

    // Outros métodos (remove, snapshot etc.) podem ser incluídos aqui com mesma lógica.
}
