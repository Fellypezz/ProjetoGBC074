
package br.ufu.facom.gbc074.kvs;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class KVSClient {
    private final KVSGrpc.KVSBlockingStub blockingStub;
    private final KVSGrpc.KVSStub asyncStub;

    public KVSClient(ManagedChannel channel) {
        blockingStub = KVSGrpc.newBlockingStub(channel);
        asyncStub = KVSGrpc.newStub(channel);
    }

    public void insert(String key, String value) {
        ChaveValor request = ChaveValor.newBuilder()
                .setChave(key)
                .setValor(value)
                .build();

        Versao response = blockingStub.insere(request);
        System.out.println("Inserido com versão: " + response.getVersao());
    }

    public void query(String key, int version) {
        ChaveVersao request = ChaveVersao.newBuilder()
                .setChave(key)
                .setVersao(version)
                .build();

        Tupla response = blockingStub.consulta(request);
        System.out.println("Consulta: " + response.getChave() + " = " +
                response.getValor() + " (v" + response.getVersao() + ")");
    }

    public void queryLatest(String key) {
        ChaveVersao request = ChaveVersao.newBuilder()
                .setChave(key)
                .build();

        Tupla response = blockingStub.consulta(request);
        System.out.println("Consulta: " + response.getChave() + " = " +
                response.getValor() + " (v" + response.getVersao() + ")");
    }

    public void remove(String key, int version) {
        ChaveVersao request = ChaveVersao.newBuilder()
                .setChave(key)
                .setVersao(version)
                .build();

        Versao response = blockingStub.remove(request);
        System.out.println("Removido versão: " + response.getVersao());
    }

    public void removeAll(String key) {
        ChaveVersao request = ChaveVersao.newBuilder()
                .setChave(key)
                .build();

        Versao response = blockingStub.remove(request);
        System.out.println("Removido todas versões: " + response.getVersao());
    }

    public void snapshot(int version) {
        Versao request = Versao.newBuilder()
                .setVersao(version)
                .build();

        final CountDownLatch finishLatch = new CountDownLatch(1);

        asyncStub.snapshot(request, new StreamObserver<Tupla>() {
            @Override
            public void onNext(Tupla tupla) {
                System.out.println("Snapshot: " + tupla.getChave() + " = " +
                        tupla.getValor() + " (v" + tupla.getVersao() + ")");
            }

            @Override
            public void onError(Throwable t) {
                System.err.println("Erro no snapshot: " + t.getMessage());
                finishLatch.countDown();
            }

            @Override
            public void onCompleted() {
                System.out.println("Snapshot completo");
                finishLatch.countDown();
            }
        });

        try {
            finishLatch.await(1, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // Métodos auxiliares para testes programáticos:

    public int insertAndGetVersion(String key, String value) {
        ChaveValor request = ChaveValor.newBuilder()
                .setChave(key)
                .setValor(value)
                .build();
        Versao response = blockingStub.insere(request);
        return response.getVersao();
    }

    public Tupla queryReturn(String key, int version) {
        ChaveVersao request = ChaveVersao.newBuilder()
                .setChave(key)
                .setVersao(version)
                .build();
        return blockingStub.consulta(request);
    }

    public Tupla queryLatestReturn(String key) {
        ChaveVersao request = ChaveVersao.newBuilder()
                .setChave(key)
                .build();
        return blockingStub.consulta(request);
    }

    public int removeAndGetVersion(String key, int version) {
        ChaveVersao request = ChaveVersao.newBuilder()
                .setChave(key)
                .setVersao(version)
                .build();
        Versao response = blockingStub.remove(request);
        return response.getVersao();
    }

    public int removeAllAndGetVersion(String key) {
        ChaveVersao request = ChaveVersao.newBuilder()
                .setChave(key)
                .build();
        Versao response = blockingStub.remove(request);
        return response.getVersao();
    }

    public List<Tupla> snapshotReturn(int version) throws InterruptedException {
        Versao request = Versao.newBuilder().setVersao(version).build();
        final List<Tupla> resultados = new ArrayList<>();
        final CountDownLatch finishLatch = new CountDownLatch(1);

        asyncStub.snapshot(request, new StreamObserver<>() {
            @Override
            public void onNext(Tupla tupla) {
                resultados.add(tupla);
            }

            @Override
            public void onError(Throwable t) {
                finishLatch.countDown();
            }

            @Override
            public void onCompleted() {
                finishLatch.countDown();
            }
        });

        finishLatch.await(1, TimeUnit.MINUTES);
        return resultados;
    }

    public static void main(String[] args) throws InterruptedException {
        String host = "localhost";
        int port = 50051;

        ManagedChannel channel = ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext()
                .build();

        try {
            KVSClient client = new KVSClient(channel);
            Scanner scanner = new Scanner(System.in);

            while (true) {
                System.out.println("\nOperações disponíveis:");
                System.out.println("1. Inserir");
                System.out.println("2. Consultar versão específica");
                System.out.println("3. Consultar última versão");
                System.out.println("4. Remover versão específica");
                System.out.println("5. Remover todas versões");
                System.out.println("6. Snapshot");
                System.out.println("0. Sair");
                System.out.print("Escolha: ");

                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                switch (choice) {
                    case 1:
                        System.out.print("Chave: ");
                        String key = scanner.nextLine();
                        System.out.print("Valor: ");
                        String value = scanner.nextLine();
                        client.insert(key, value);
                        break;

                    case 2:
                        System.out.print("Chave: ");
                        key = scanner.nextLine();
                        System.out.print("Versão: ");
                        int version = scanner.nextInt();
                        client.query(key, version);
                        break;

                    case 3:
                        System.out.print("Chave: ");
                        key = scanner.nextLine();
                        client.queryLatest(key);
                        break;

                    case 4:
                        System.out.print("Chave: ");
                        key = scanner.nextLine();
                        System.out.print("Versão: ");
                        version = scanner.nextInt();
                        client.remove(key, version);
                        break;

                    case 5:
                        System.out.print("Chave: ");
                        key = scanner.nextLine();
                        client.removeAll(key);
                        break;

                    case 6:
                        System.out.print("Versão (0 para última): ");
                        version = scanner.nextInt();
                        client.snapshot(version);
                        break;

                    case 0:
                        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
                        return;

                    default:
                        System.out.println("Opção inválida");
                }
            }
        } finally {
            channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
        }
    }
}
