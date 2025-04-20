package br.ufu.facom.gbc074.kvs;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import java.io.IOException;
import java.util.logging.Logger;

public class Main {
    private static Logger LOG = Logger.getLogger("Logger");

    public static void main(String[] args) throws InterruptedException, IOException {
        if (args.length < 1) {
            System.err.println("Uso: java br.ufu.facom.gbc074.kvs.Main <porta>");
            return;
        }

        int porta = Integer.parseInt(args[0]);

        LOG.info("Iniciando servidor gRPC KVS na porta " + porta + "...");

        try {
            Server server = ServerBuilder.forPort(porta)
                    .addService(new KVSService()) // Certifique-se que o construtor KVSService NÃO tem throws MqttException
                    .build();

            server.start();
            LOG.info("Servidor KVS iniciado na porta " + porta + "!");

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                LOG.warning("Recebido sinal para desligar o servidor...");
                server.shutdown();
                LOG.info("Servidor KVS desligado com sucesso!");
            }));

            server.awaitTermination();
        } catch (Exception e) {
            LOG.severe("Erro inesperado ao iniciar o servidor: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
