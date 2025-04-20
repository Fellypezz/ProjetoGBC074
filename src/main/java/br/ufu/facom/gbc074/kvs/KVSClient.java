
package br.ufu.facom.gbc074.kvs;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.util.Scanner;

public class KVSClient {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        ManagedChannel channel = ManagedChannelBuilder
                .forAddress("localhost", 50051)
                .usePlaintext()
                .build();

        KVSGrpc.KVSBlockingStub stub = KVSGrpc.newBlockingStub(channel);

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
            int opcao = Integer.parseInt(sc.nextLine());

            switch (opcao) {
                case 1:
                    insert(stub, sc);
                    break;
                case 2:
                    consultaVersaoEspecifica(stub, sc);
                    break;
                case 3:
                    consultaUltimaVersao(stub, sc);
                    break;
                case 4:
                    removeVersaoEspecifica(stub, sc);
                    break;
                case 5:
                    removeTodasVersoes(stub, sc);
                    break;
                case 6:
                    snapshot(stub, sc);
                    break;
                case 0:
                    channel.shutdown();
                    return;
                default:
                    System.out.println("Opção inválida.");
            }
        }
    }

    private static void insert(KVSGrpc.KVSBlockingStub stub, Scanner sc) {
        System.out.print("Chave: ");
        String chave = sc.nextLine();
        System.out.print("Valor: ");
        String valor = sc.nextLine();

        ChaveValor request = ChaveValor.newBuilder().setChave(chave).setValor(valor).build();
        Versao response = stub.insere(request);

        System.out.println("Inserido com versão: " + response.getVersao());
    }

    private static void consultaVersaoEspecifica(KVSGrpc.KVSBlockingStub stub, Scanner sc) {
        System.out.print("Chave: ");
        String chave = sc.nextLine();
        System.out.print("Versão: ");
        int versao = Integer.parseInt(sc.nextLine());

        ChaveVersao request = ChaveVersao.newBuilder().setChave(chave).setVersao(versao).build();
        Tupla resposta = stub.consulta(request);

        if (!resposta.getChave().isEmpty() && resposta.getVersao() > 0) {
            System.out.printf("Consulta: %s = %s (v%d)%n",
                    resposta.getChave(), resposta.getValor(), resposta.getVersao());
        } else {
            System.out.println("❌ Valor não encontrado para essa versão.");
        }
    }

    private static void consultaUltimaVersao(KVSGrpc.KVSBlockingStub stub, Scanner sc) {
        System.out.print("Chave: ");
        String chave = sc.nextLine();

        ChaveVersao request = ChaveVersao.newBuilder()
                .setChave(chave)
                .setVersao(-1)  // Última versão
                .build();

        Tupla resposta = stub.consulta(request);

        if (!resposta.getChave().isEmpty() && resposta.getVersao() > 0) {
            System.out.printf("Consulta: %s = %s (v%d)%n",
                    resposta.getChave(), resposta.getValor(), resposta.getVersao());
        } else {
            System.out.println("❌ Nenhuma versão encontrada.");
        }
    }

    private static void removeVersaoEspecifica(KVSGrpc.KVSBlockingStub stub, Scanner sc) {
        System.out.print("Chave: ");
        String chave = sc.nextLine();
        System.out.print("Versão: ");
        int versao = Integer.parseInt(sc.nextLine());

        ChaveVersao request = ChaveVersao.newBuilder().setChave(chave).setVersao(versao).build();
        Versao resposta = stub.remove(request);

        System.out.println("Remoção: versão retornada = " + resposta.getVersao());
    }

    private static void removeTodasVersoes(KVSGrpc.KVSBlockingStub stub, Scanner sc) {
        System.out.print("Chave: ");
        String chave = sc.nextLine();

        ChaveVersao request = ChaveVersao.newBuilder().setChave(chave).setVersao(-1).build();
        Versao resposta = stub.remove(request);

        System.out.println("Remoção total: versão retornada = " + resposta.getVersao());
    }

    private static void snapshot(KVSGrpc.KVSBlockingStub stub, Scanner sc) {
        System.out.print("Versão limite (0 para última): ");
        int versao = Integer.parseInt(sc.nextLine());

        Versao request = Versao.newBuilder().setVersao(versao).build();
        stub.snapshot(request).forEachRemaining(tupla -> {
            System.out.printf("Snapshot: %s = %s (v%d)%n",
                    tupla.getChave(), tupla.getValor(), tupla.getVersao());
        });
    }
}
