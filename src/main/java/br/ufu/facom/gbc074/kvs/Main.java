package br.ufu.facom.gbc074.kvs;

import org.eclipse.paho.client.mqttv3.MqttException;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import java.io.IOException;
import java.util.logging.Logger;

public class Main {
    private static final Logger LOG = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) throws InterruptedException, IOException {
        LOG.info("Iniciando servidor gRPC KVS...");

        try {
            // Cria e inicia o serviço KVS na porta 50051
            Server server = ServerBuilder.forPort(50051)
                    .addService(new KVSService())
                    .build();

            server.start();
            LOG.info("Servidor KVS iniciado na porta 50051!");

            // Adiciona hook para desligamento gracioso
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                LOG.warning("Recebido sinal para desligar o servidor...");
                server.shutdown();
                LOG.info("Servidor KVS desligado com sucesso!");
            }));

            // Mantém o servidor rodando
            server.awaitTermination();
        } catch (MqttException e) {
            LOG.severe("Erro ao iniciar o serviço MQTT: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            LOG.severe("Erro inesperado: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
