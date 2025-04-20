package br.ufu.facom.gbc074.kvs;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class KVSClient {

    private final KVSGrpc.KVSBlockingStub stub;

    public KVSClient(ManagedChannel channel) {
        this.stub = KVSGrpc.newBlockingStub(channel);
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        ManagedChannel channel = ManagedChannelBuilder
                .forAddress("localhost", 50051)
                .usePlaintext()
                .build();

        KVSClient client = new KVSClient(channel);

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
                    System.out.print("Chave: ");
                    String chave1 = sc.nextLine();
                    System.out.print("Valor: ");
                    String valor = sc.nextLine();
                    int versao = client.insertAndGetVersion(chave1, valor);
                    System.out.println("Inserido com versão: " + versao);
                    break;
                case 2:
                    System.out.print("Chave: ");
                    String chave2 = sc.nextLine();
                    System.out.print("Versão: ");
                    int versaoConsulta = Integer.parseInt(sc.nextLine());
                    System.out.println("Consulta: " + client.queryReturn(chave2, versaoConsulta));
                    break;
                case 3:
                    System.out.print("Chave: ");
                    String chave3 = sc.nextLine();
                    System.out.println("Consulta: " + client.queryLatestReturn(chave3));
                    break;
                case 4:
                    System.out.print("Chave: ");
                    String chave4 = sc.nextLine();
                    System.out.print("Versão: ");
                    int versaoRemover = Integer.parseInt(sc.nextLine());
                    int retornoRemover = client.removeAndGetVersion(chave4, versaoRemover);
                    System.out.println("Remoção: versão retornada = " + retornoRemover);
                    break;
                case 5:
                    System.out.print("Chave: ");
                    String chave5 = sc.nextLine();
                    int retornoTotal = client.removeAllAndGetVersion(chave5);
                    System.out.println("Remoção total: versão retornada = " + retornoTotal);
                    break;
                case 6:
                    System.out.print("Versão limite (0 para última): ");
                    int limite = Integer.parseInt(sc.nextLine());
                    List<Tupla> tuplas = client.snapshotReturn(limite);
                    for (Tupla t : tuplas) {
                        System.out.printf("Snapshot: %s = %s (v%d)%n",
                                t.getChave(), t.getValor(), t.getVersao());
                    }
                    break;
                case 0:
                    channel.shutdown();
                    return;
                default:
                    System.out.println("Opção inválida.");
            }
        }
    }

    public int insertAndGetVersion(String chave, String valor) {
        ChaveValor request = ChaveValor.newBuilder().setChave(chave).setValor(valor).build();
        Versao response = stub.insere(request);
        return response.getVersao();
    }

    public String queryReturn(String chave, int versao) {
        ChaveVersao request = ChaveVersao.newBuilder().setChave(chave).setVersao(versao).build();
        Tupla resposta = stub.consulta(request);
        if (!resposta.getChave().isEmpty() && resposta.getVersao() > 0) {
            return String.format("%s = %s (v%d)", resposta.getChave(), resposta.getValor(), resposta.getVersao());
        } else {
            return "❌ Valor não encontrado.";
        }
    }

    public String queryLatestReturn(String chave) {
        return queryReturn(chave, -1);
    }

    public int removeAndGetVersion(String chave, int versao) {
        ChaveVersao request = ChaveVersao.newBuilder().setChave(chave).setVersao(versao).build();
        Versao resposta = stub.remove(request);
        return resposta.getVersao();
    }

    public int removeAllAndGetVersion(String chave) {
        return removeAndGetVersion(chave, -1);
    }

    public List<Tupla> snapshotReturn(int versaoLimite) {
        Versao request = Versao.newBuilder().setVersao(versaoLimite).build();
        List<Tupla> resultado = new ArrayList<>();
        stub.snapshot(request).forEachRemaining(resultado::add);
        return resultado;
    }
}
