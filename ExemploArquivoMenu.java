import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

/*
 * =====================================================================
 *  EXEMPLOARQUIVOMENU.JAVA  –  PROGRAMA INTEIRO: ARQUIVO + MENU
 * =====================================================================
 *  Simula o segundo formato típico de enunciado:
 *
 *   "Um sistema de vendas guarda pedidos no arquivo pedidos.csv
 *    (codigo;cliente;valor;quantidade). Leia o arquivo e ofereça um menu
 *    em que o usuário escolhe o critério e o método de ordenação.
 *    Mostre os pedidos ordenados e as estatísticas. Tenha também uma
 *    opção que compare todos os métodos."
 *
 *  PRECISA DE: Ordenacao.java na mesma pasta.
 *
 *  NA PROVA: troque "Pedido" pela classe do enunciado (Ctrl+H, com Aa ligado),
 *  ajuste PARTE 1 (atributos), PARTE 2 (critérios), PARTE 3 (arquivo)
 *  e PARTE 4 (opções do menu de critério).
 *
 *  RODAR:  javac -d out *.java   depois   java -cp out ExemploArquivoMenu
 *  O arquivo pedidos.csv é criado sozinho na primeira vez (na pasta onde rodou).
 * =====================================================================
 */


/* =====================================================================
 * PARTE 1 – A CLASSE DO SISTEMA
 * ===================================================================== */
class Pedido {

    private int codigo;
    private String cliente;
    private double valor;
    private int quantidade;

    public Pedido(int codigo, String cliente, double valor, int quantidade) {
        this.codigo = codigo;
        this.cliente = cliente;
        this.valor = valor;
        this.quantidade = quantidade;
    }

    public int getCodigo()      { return codigo; }
    public String getCliente()  { return cliente; }
    public double getValor()    { return valor; }
    public int getQuantidade()  { return quantidade; }

    /* =================================================================
     * PARTE 2 – CRITÉRIOS
     * ================================================================= */
    public static final Comparator<Pedido> POR_CODIGO     = Comparator.comparingInt(Pedido::getCodigo);
    public static final Comparator<Pedido> POR_CLIENTE    = Comparator.comparing(Pedido::getCliente);
    public static final Comparator<Pedido> POR_VALOR      = Comparator.comparingDouble(Pedido::getValor);
    public static final Comparator<Pedido> POR_QUANTIDADE = Comparator.comparingInt(Pedido::getQuantidade);
    // maior valor primeiro; empate -> cliente A-Z
    public static final Comparator<Pedido> POR_VALOR_DESC_CLIENTE =
            POR_VALOR.reversed().thenComparing(POR_CLIENTE);

    /* =================================================================
     * PARTE 3 – CONVERTER LINHA DO ARQUIVO EM OBJETO
     * "101;Maria;250,90;3" -> c[0]=101, c[1]=Maria, c[2]=250,90, c[3]=3
     * Se a ordem das colunas do arquivo da prova for outra, troque os índices.
     * ================================================================= */
    public static Pedido deCSV(String[] c) {
        return new Pedido(
                Integer.parseInt(c[0].trim()),
                c[1].trim(),
                Double.parseDouble(c[2].trim().replace(",", ".")),
                Integer.parseInt(c[3].trim()));
    }

    @Override
    public String toString() {
        return String.format("cod: %-5d cliente: %-10s valor: R$ %9.2f  qtd: %3d",
                codigo, cliente, valor, quantidade);
    }
}


/* =====================================================================
 * PROGRAMA PRINCIPAL
 * ===================================================================== */
public class ExemploArquivoMenu {

    static final String ARQUIVO = "pedidos.csv";   // nome do arquivo da prova

    public static void main(String[] args) {
        criarArquivoExemploSeNaoExistir();          // na prova: apague esta linha se ela der o arquivo

        // ---------- lê o arquivo ----------
        List<Pedido> pedidos;
        try {
            pedidos = LeitorArquivo.lerCSV(ARQUIVO, ";", true, Pedido::deCSV);
        } catch (IOException e) {
            System.out.println("Erro ao ler " + ARQUIVO + ": " + e.getMessage());
            return;
        }
        System.out.println(pedidos.size() + " pedidos lidos de " + ARQUIVO);

        // ---------- menu ----------
        Scanner sc = new Scanner(System.in);
        int opcao;
        do {
            System.out.println("\n===== MENU =====");
            System.out.println("1 - Ordenar (escolher criterio e metodo)");
            System.out.println("2 - Comparar todos os metodos");
            System.out.println("3 - Mostrar pedidos sem ordenar");
            System.out.println("0 - Sair");
            opcao = lerInt(sc);

            switch (opcao) {
                case 1: ordenarEscolhendo(pedidos, sc); break;
                case 2: compararTodos(pedidos, sc); break;
                case 3: imprimir(pedidos); break;
                case 0: System.out.println("Saindo..."); break;
                default: System.out.println("Opcao invalida");
            }
        } while (opcao != 0);
        sc.close();
    }

    /* =================================================================
     * PARTE 4 – MENU DE CRITÉRIOS (uma opção por critério da PARTE 2)
     * ================================================================= */
    static Comparator<Pedido> escolherCriterio(Scanner sc) {
        while (true) {
            System.out.println("Criterio: 1-Codigo 2-Cliente 3-Valor 4-Quantidade 5-Valor decrescente");
            switch (lerInt(sc)) {
                case 1: return Pedido.POR_CODIGO;
                case 2: return Pedido.POR_CLIENTE;
                case 3: return Pedido.POR_VALOR;
                case 4: return Pedido.POR_QUANTIDADE;
                case 5: return Pedido.POR_VALOR_DESC_CLIENTE;
                default: System.out.println("Opcao invalida");
            }
        }
    }

    /** Opção 1: usuário escolhe critério + método; mostra resultado e estatísticas. */
    static void ordenarEscolhendo(List<Pedido> pedidos, Scanner sc) {
        Comparator<Pedido> criterio = escolherCriterio(sc);
        System.out.println("Metodo: " + FabricaOrdenador.MENU);
        try {
            Ordenador<Pedido> o = FabricaOrdenador.criar(lerInt(sc), criterio); // POLIMORFISMO
            List<Pedido> copia = new ArrayList<>(pedidos);   // não estraga a lista original
            o.executar(copia);
            System.out.println("\n--- Ordenado com " + o.getNome() + " ---");
            imprimir(copia);
            System.out.println(o);
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    /** Opção 2: mesmo critério nos 6 métodos, mostra estatísticas de cada um. */
    static void compararTodos(List<Pedido> pedidos, Scanner sc) {
        Comparator<Pedido> criterio = escolherCriterio(sc);
        System.out.println("\n--- Comparacao dos metodos ---");
        for (int m = 1; m <= 6; m++) {
            Ordenador<Pedido> o = FabricaOrdenador.criar(m, criterio);
            List<Pedido> copia = new ArrayList<>(pedidos);
            o.executar(copia);
            System.out.println(o);
        }
    }

    // ----------------------------- utilitários -----------------------------

    static void imprimir(List<Pedido> lista) {
        for (Pedido p : lista) System.out.println("  " + p);
    }

    /** Lê número sem quebrar se digitarem letra. */
    static int lerInt(Scanner sc) {
        while (!sc.hasNextInt()) { sc.next(); System.out.print("Digite um numero: "); }
        return sc.nextInt();
    }

    /** Cria um pedidos.csv de exemplo. Na prova, a professora deve dar o arquivo. */
    static void criarArquivoExemploSeNaoExistir() {
        Path p = Paths.get(ARQUIVO);
        if (Files.exists(p)) return;
        try {
            Files.write(p, Arrays.asList(
                    "codigo;cliente;valor;quantidade",
                    "105;Maria;250,90;3",
                    "101;Joao;89,90;1",
                    "110;Ana;1200,00;2",
                    "103;Pedro;250,90;5",
                    "108;Carla;45,50;10",
                    "102;Bruno;599,99;1"), StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.out.println("Nao consegui criar o arquivo de exemplo: " + e.getMessage());
        }
    }
}
