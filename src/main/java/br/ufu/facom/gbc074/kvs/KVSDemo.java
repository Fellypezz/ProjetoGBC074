package br.ufu.facom.gbc074.kvs;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class KVSDemo {
    public static void main(String[] args) throws InterruptedException {
        String host = "localhost";
        int port = 50051;

        System.out.println(" Iniciando demonstração KVS...");

        ManagedChannel channel = ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext()
                .build();

        KVSClient client = new KVSClient(channel);

        System.out.println("\n Inserindo chave 'aluno'...");
        int versao1 = client.insertAndGetVersion("aluno", "joao");
        System.out.println("Inserido: chave = 'aluno', valor = 'joao', versão = " + versao1);

        int versao2 = client.insertAndGetVersion("aluno", "maria");
        System.out.println("Inserido: chave = 'aluno', valor = 'maria', versão = " + versao2);

        System.out.println("\n Consultando versão 1:");
        System.out.println(client.queryReturn("aluno", versao1));

        System.out.println("\n Consultando última versão:");
        System.out.println(client.queryLatestReturn("aluno"));

        System.out.println("\n🗑 Removendo versão " + versao1 + ":");
        int versaoRemovida = client.removeAndGetVersion("aluno", versao1);
        System.out.println("Removida versão: " + versaoRemovida);

        System.out.println("\n🧹 Removendo todas as versões:");
        int versaoClear = client.removeAllAndGetVersion("aluno");
        System.out.println("Limpeza realizada (retorno: " + versaoClear + ")");

        System.out.println("\n Inserindo novas chaves para snapshot...");
        int versaoCidade = client.insertAndGetVersion("cidade", "Uberlandia");
        System.out.println("Inserido: chave = 'cidade', valor = 'Uberlandia', versão = " + versaoCidade);

        int versaoEstado = client.insertAndGetVersion("estado", "MG");
        System.out.println("Inserido: chave = 'estado', valor = 'MG', versão = " + versaoEstado);

        System.out.println("\n Executando snapshot:");
        List<Tupla> snapshot = client.snapshotReturn(0);
        for (Tupla t : snapshot) {
            System.out.println(t.getChave() + " = " + t.getValor() + " (v" + t.getVersao() + ")");
        }

        System.out.println("\n Demonstração finalizada com sucesso!");

        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }
}
