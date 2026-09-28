import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

/*
 * =====================================================================
 *  MAIN.JAVA  –  exemplos prontos de TUDO que pode cair.
 * =====================================================================
 *  Cada método abaixo é um "cenário" de prova. Na hora, copie o trecho
 *  do cenário parecido com o enunciado e troque Produto pela sua classe.
 *
 *   exemploBasico()        -> ordenar um vetor com UM método e UM critério
 *   exemploPolimorfismo()  -> vários métodos numa lista do tipo Ordenador
 *   exemploCriterios()     -> mesmo método, critérios diferentes/decrescente
 *   exemploLista()         -> ordenar ArrayList em vez de vetor
 *   exemploArquivo()       -> ler dados de arquivo e ordenar
 *   exemploDesempenho()    -> comparar tempo/comparações dos métodos
 *   menuInterativo()       -> usuário escolhe critério e método
 * =====================================================================
 */
public class Main {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int op;
        do {
            System.out.println("\n1-Demonstracao completa  2-Menu interativo  0-Sair");
            op = lerInt(sc);
            if (op == 1) {
                exemploBasico();
                exemploPolimorfismo();
                exemploCriterios();
                exemploLista();
                exemploArquivo();
                exemploDesempenho();
            } else if (op == 2) {
                menuInterativo(sc);
            }
        } while (op != 0);
        sc.close();
    }

    /** Dados de exemplo. Na prova: troque pelos dados do enunciado. */
    static Produto[] dados() {
        return new Produto[] {
            new Produto("Teclado", 150.00, 12),
            new Produto("Mouse", 80.50, 30),
            new Produto("Monitor", 899.90, 5),
            new Produto("Cabo HDMI", 25.00, 50),
            new Produto("Webcam", 150.00, 8),
            new Produto("Headset", 220.00, 15),
        };
    }

    // ---------------------------------------------------------------
    // CENÁRIO 1: o mais simples. "Ordene os produtos por preço usando X".
    // ---------------------------------------------------------------
    static void exemploBasico() {
        titulo("Basico: Quicksort por preco");
        Produto[] v = dados();
        Ordenador<Produto> o = new Quicksort<>(Produto.POR_PRECO);
        o.executar(v);
        imprimir(v);
        System.out.println(o);  // estatísticas
    }

    // ---------------------------------------------------------------
    // CENÁRIO 2: POLIMORFISMO. "Use polimorfismo / aplique vários métodos".
    // A lista é do tipo da classe-mãe e guarda qualquer método.
    // ---------------------------------------------------------------
    static void exemploPolimorfismo() {
        titulo("Polimorfismo: todos os metodos por preco");
        List<Ordenador<Produto>> ordenadores = new ArrayList<>();
        ordenadores.add(new Selecao<>(Produto.POR_PRECO));
        ordenadores.add(new Bolha<>(Produto.POR_PRECO));
        ordenadores.add(new Insercao<>(Produto.POR_PRECO));
        ordenadores.add(new Mergesort<>(Produto.POR_PRECO));
        ordenadores.add(new Heapsort<>(Produto.POR_PRECO));
        ordenadores.add(new Quicksort<>(Produto.POR_PRECO));

        for (Ordenador<Produto> o : ordenadores) {
            Produto[] copia = dados();   // sempre ordenar uma CÓPIA do original
            o.executar(copia);           // chamada polimórfica
            System.out.println(o + "  ok=" + o.estaOrdenado(copia));
        }
    }

    // ---------------------------------------------------------------
    // CENÁRIO 3: CRITÉRIOS. "Ordene por nome, depois por estoque, decrescente..."
    // Mesmo algoritmo, só troca o Comparator.
    // ---------------------------------------------------------------
    static void exemploCriterios() {
        titulo("Criterios: Mergesort por preco DESC, desempate por nome");
        Produto[] v = dados();
        new Mergesort<>(Produto.POR_PRECO_DESC_NOME).ordenar(v);
        imprimir(v);

        titulo("Criterios: Heapsort por estoque");
        v = dados();
        Ordenador<Produto> o = new Heapsort<>(Produto.POR_ESTOQUE);
        o.ordenar(v);
        imprimir(v);

        titulo("Criterios: mesmo objeto, troca o criterio com setComparador (nome)");
        o.setComparador(Produto.POR_NOME);
        o.ordenar(v);
        imprimir(v);
    }

    // ---------------------------------------------------------------
    // CENÁRIO 4: LISTA. Quando os dados estão num ArrayList.
    // ---------------------------------------------------------------
    static void exemploLista() {
        titulo("ArrayList por nome (Insercao)");
        List<Produto> lista = new ArrayList<>(Arrays.asList(dados()));
        IOrdenador<Produto> o = new Insercao<>(Produto.POR_NOME);  // tipo = interface
        o.ordenar(lista);
        for (Produto p : lista) System.out.println("  " + p);
    }

    // ---------------------------------------------------------------
    // CENÁRIO 5: ARQUIVO. "Leia os dados do arquivo X e ordene".
    // Aqui eu CRIO um arquivo de exemplo se não existir; na prova, use o dela.
    // O arquivo fica na pasta do projeto (a mesma do pom.xml / build.xml).
    // ---------------------------------------------------------------
    static void exemploArquivo() {
        titulo("Arquivo produtos.csv por preco (Quicksort)");
        try {
            Path arq = Paths.get("produtos.csv");
            if (!Files.exists(arq)) {
                Files.write(arq, Arrays.asList(
                        "nome;preco;estoque",
                        "Teclado;150,00;12",
                        "Mouse;80,50;30",
                        "Monitor;899,90;5",
                        "Cabo HDMI;25,00;50"), StandardCharsets.UTF_8);
            }
            List<Produto> lista = LeitorArquivo.lerCSV("produtos.csv", ";", true, Produto::deCSV);
            new Quicksort<>(Produto.POR_PRECO).ordenar(lista);
            for (Produto p : lista) System.out.println("  " + p);
        } catch (IOException e) {
            System.out.println("  Erro ao ler arquivo: " + e.getMessage());
        }
    }

    // ---------------------------------------------------------------
    // CENÁRIO 6: DESEMPENHO. "Compare os métodos / meça tempo e comparações".
    // Testa vetor aleatório, já ordenado (melhor caso) e inverso (pior caso).
    // ---------------------------------------------------------------
    static void exemploDesempenho() {
        int n = 5000;                       // tamanho do teste (pode mudar)
        Random r = new Random(42);
        Integer[] aleatorio = new Integer[n], crescente = new Integer[n], inverso = new Integer[n];
        for (int i = 0; i < n; i++) {
            aleatorio[i] = r.nextInt(100000);
            crescente[i] = i;
            inverso[i] = n - i;
        }
        compararMetodos("aleatorio", aleatorio);
        compararMetodos("ja ordenado", crescente);
        compararMetodos("inverso", inverso);
    }

    static void compararMetodos(String nome, Integer[] dados) {
        titulo("Desempenho: " + dados.length + " inteiros, " + nome);
        Comparator<Integer> natural = Comparator.naturalOrder(); // ordem normal de números
        for (int op = 1; op <= 6; op++) {
            Ordenador<Integer> o = FabricaOrdenador.criar(op, natural);
            Integer[] copia = dados.clone();
            o.executar(copia);
            System.out.println(o + "  ok=" + o.estaOrdenado(copia));
        }
    }

    // ---------------------------------------------------------------
    // CENÁRIO 7: MENU. "O usuário deve escolher o critério e o método".
    // ---------------------------------------------------------------
    static void menuInterativo(Scanner sc) {
        while (true) {
            System.out.println("\nCriterio: 1-Nome 2-Preco 3-Estoque 4-Preco decrescente 0-Voltar");
            int crit = lerInt(sc);
            if (crit == 0) return;

            Comparator<Produto> c;
            switch (crit) {
                case 1:  c = Produto.POR_NOME; break;
                case 2:  c = Produto.POR_PRECO; break;
                case 3:  c = Produto.POR_ESTOQUE; break;
                case 4:  c = Produto.POR_PRECO_DESC; break;
                default: System.out.println("Opcao invalida"); continue;
            }

            System.out.println("Metodo: " + FabricaOrdenador.MENU);
            try {
                Ordenador<Produto> o = FabricaOrdenador.criar(lerInt(sc), c);
                Produto[] v = dados();
                o.executar(v);
                imprimir(v);
                System.out.println(o);
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    // ------------------------- utilitários -------------------------

    /** Lê um inteiro sem quebrar se o usuário digitar letra. */
    static int lerInt(Scanner sc) {
        while (!sc.hasNextInt()) { sc.next(); System.out.print("Digite um numero: "); }
        return sc.nextInt();
    }

    /** Imprime qualquer vetor (funciona com Produto, Integer, String...). */
    static <T> void imprimir(T[] v) {
        for (T x : v) System.out.println("  " + x);
    }

    static void titulo(String t) {
        System.out.println("\n== " + t + " ==");
    }
}
