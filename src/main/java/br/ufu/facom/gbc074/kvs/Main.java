package br.ufu.facom.gbc074.kvs;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import java.io.IOException;
import java.util.logging.Logger;
import org.eclipse.paho.client.mqttv3.MqttException;

// IMPORTANTE: esse import precisa existir
import br.ufu.facom.gbc074.kvs.KVSGrpc;

public class Main {
    private static final Logger LOG = Logger.getLogger("Logger");

    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Uso: java br.ufu.facom.gbc074.kvs.Main <porta>");
            return;
        }

        int porta = Integer.parseInt(args[0]);
        LOG.info("Iniciando servidor gRPC KVS na porta " + porta + "...");

        try {
            KVSService servico = new KVSService();

            Server server = ServerBuilder
                    .forPort(porta)
                    .addService((KVSGrpc.KVSImplBase) servico) // 👈 aqui o cast resolve o erro
                    .build();

            server.start();
            LOG.info("Servidor KVS iniciado na porta " + porta + "!");

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                LOG.warning("Recebido sinal para desligar o servidor...");
                server.shutdown();
                LOG.info("Servidor KVS desligado com sucesso!");
            }));

            server.awaitTermination();
        } catch (MqttException e) {
            LOG.severe("Erro ao iniciar o serviço MQTT: " + e.getMessage());
            e.printStackTrace();
        } catch (IOException | InterruptedException e) {
            LOG.severe("Erro ao iniciar o servidor gRPC: " + e.getMessage());
            e.printStackTrace();
        }
    }
}