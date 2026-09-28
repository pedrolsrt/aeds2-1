import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

/*
 * =====================================================================
 *  ORDENACAO.JAVA  –  "motor" de ordenação. NÃO PRECISA MEXER AQUI.
 * =====================================================================
 *  Tem tudo num arquivo só: interface, classe abstrata, os 6 métodos,
 *  a fábrica (pra menu) e o leitor de arquivo.
 *
 *  COMO USAR (resumo):
 *    Ordenador<Produto> o = new Quicksort<>(Produto.POR_PRECO);
 *    o.executar(vetor);          // ordena + conta + mede tempo
 *    System.out.println(o);      // mostra comparações, movimentações e tempo
 *
 *  QUAL MÉTODO ESCOLHER?
 *    - Enunciado mandou um específico  -> usa o que ele mandou.
 *    - Precisa ser ESTÁVEL (manter ordem de empates) -> Insercao ou Mergesort.
 *    - Vetor grande, quer rápido       -> Quicksort (ou Mergesort/Heapsort).
 *    - Vetor pequeno/quase ordenado     -> Insercao.
 *    - Pouca memória extra              -> Heapsort.
 *    - Selecao e Bolha: só se pedirem (são os mais lentos).
 * =====================================================================
 */


/* ---------------------------------------------------------------------
 * IOrdenador  (INTERFACE)
 * O que é: o "contrato" – lista o que todo ordenador tem que ter.
 * Quando usar: se o enunciado pedir "crie uma interface", é esta.
 *              Também pode declarar variável assim: IOrdenador<Produto> o = ...
 * ------------------------------------------------------------------- */
interface IOrdenador<T> {
    void ordenar(T[] vetor);
    void ordenar(List<T> lista);
    String getNome();
    long getComparacoes();
    long getMovimentacoes();
    double getTempoMs();
}


/* ---------------------------------------------------------------------
 * Ordenador  (CLASSE ABSTRATA – a base de todos)
 * O que é: a "classe-mãe" dos 6 métodos. Guarda o critério (Comparator),
 *          conta comparações/movimentações e mede o tempo.
 * Por que é POLIMORFISMO: Selecao, Quicksort etc. "são" Ordenador, então
 *          uma variável/lista do tipo Ordenador<T> guarda qualquer um deles
 *          e ordenar() roda a versão certa sozinho.
 * Quando usar: sempre como tipo da variável ou da lista de ordenadores.
 * ------------------------------------------------------------------- */
abstract class Ordenador<T> implements IOrdenador<T> {

    protected Comparator<? super T> comparador; // critério: por nome, preço...
    protected long comparacoes;
    protected long movimentacoes;
    protected double tempoMs;

    public Ordenador(Comparator<? super T> comparador) {
        this.comparador = comparador;
    }

    // Cada método (Selecao, Bolha...) implementa o seu.
    @Override public abstract void ordenar(T[] vetor);
    @Override public abstract String getNome();

    /** Ordena uma List (ArrayList). Use quando os dados estiverem em lista, não vetor. */
    @Override
    @SuppressWarnings("unchecked")
    public void ordenar(List<T> lista) {
        T[] v = (T[]) lista.toArray();
        ordenar(v);
        for (int i = 0; i < v.length; i++) lista.set(i, v[i]);
    }

    /** Zera contadores + ordena + mede tempo. Use quando pedirem estatísticas/comparar métodos. */
    public void executar(T[] v) {
        zerarContadores();
        long ini = System.nanoTime();
        ordenar(v);
        tempoMs = (System.nanoTime() - ini) / 1_000_000.0;
    }

    /** Mesmo que o de cima, mas pra List. */
    public void executar(List<T> lista) {
        zerarContadores();
        long ini = System.nanoTime();
        ordenar(lista);
        tempoMs = (System.nanoTime() - ini) / 1_000_000.0;
    }

    /** Compara dois elementos pelo critério e CONTA a comparação. (<0 menor, 0 igual, >0 maior) */
    protected int comparar(T a, T b) {
        comparacoes++;
        return comparador.compare(a, b);
    }

    /** Troca duas posições do vetor. Conta 3 movimentações (tmp=a; a=b; b=tmp). */
    protected void trocar(T[] v, int i, int j) {
        T tmp = v[i];
        v[i] = v[j];
        v[j] = tmp;
        movimentacoes += 3;
    }

    /** Diz se o vetor ficou ordenado. Use pra testar/provar que funcionou. */
    public boolean estaOrdenado(T[] v) {
        for (int i = 1; i < v.length; i++)
            if (comparador.compare(v[i - 1], v[i]) > 0) return false;
        return true;
    }

    public void zerarContadores() { comparacoes = 0; movimentacoes = 0; tempoMs = 0; }

    /** Troca o critério sem criar outro objeto. */
    public void setComparador(Comparator<? super T> c) { this.comparador = c; }

    @Override public long getComparacoes()   { return comparacoes; }
    @Override public long getMovimentacoes() { return movimentacoes; }
    @Override public double getTempoMs()     { return tempoMs; }

    @Override
    public String toString() {
        return String.format("%-10s comparacoes=%-9d movimentacoes=%-9d tempo=%.3f ms",
                getNome(), comparacoes, movimentacoes, tempoMs);
    }
}


/* ---------------------------------------------------------------------
 * SELEÇÃO
 * O que faz: acha o MENOR do resto e coloca na posição i.
 * Custo: O(n²) sempre. Poucas trocas (no máx. n-1). Não estável.
 * Quando usar: quando pedirem; ou quando trocar for "caro" (poucas trocas).
 * ------------------------------------------------------------------- */
class Selecao<T> extends Ordenador<T> {
    public Selecao(Comparator<? super T> c) { super(c); }

    @Override
    public void ordenar(T[] v) {
        for (int i = 0; i < v.length - 1; i++) {
            int menor = i;
            for (int j = i + 1; j < v.length; j++)
                if (comparar(v[j], v[menor]) < 0) menor = j;
            if (menor != i) trocar(v, i, menor);
        }
    }

    @Override public String getNome() { return "Selecao"; }
}


/* ---------------------------------------------------------------------
 * BOLHA (Bubble sort)
 * O que faz: troca vizinhos fora de ordem; o maior "sobe" pro fim a cada passada.
 *            Para cedo se uma passada não trocar nada.
 * Custo: O(n²); O(n) se já estiver ordenado. Estável.
 * Quando usar: só se pedirem (é o mais lento na prática).
 * ------------------------------------------------------------------- */
class Bolha<T> extends Ordenador<T> {
    public Bolha(Comparator<? super T> c) { super(c); }

    @Override
    public void ordenar(T[] v) {
        for (int i = v.length - 1; i > 0; i--) {
            boolean trocou = false;
            for (int j = 0; j < i; j++) {
                if (comparar(v[j], v[j + 1]) > 0) {
                    trocar(v, j, j + 1);
                    trocou = true;
                }
            }
            if (!trocou) break;
        }
    }

    @Override public String getNome() { return "Bolha"; }
}


/* ---------------------------------------------------------------------
 * INSERÇÃO
 * O que faz: pega v[i] e "encaixa" na parte da esquerda que já está ordenada
 *            (igual organizar cartas na mão).
 * Custo: O(n²); O(n) se quase ordenado. Estável.
 * Quando usar: vetor pequeno ou quase ordenado; quando precisar de estabilidade.
 * ------------------------------------------------------------------- */
class Insercao<T> extends Ordenador<T> {
    public Insercao(Comparator<? super T> c) { super(c); }

    @Override
    public void ordenar(T[] v) {
        for (int i = 1; i < v.length; i++) {
            T tmp = v[i];
            movimentacoes++;
            int j = i - 1;
            while (j >= 0 && comparar(v[j], tmp) > 0) {
                v[j + 1] = v[j];            // empurra pra direita
                movimentacoes++;
                j--;
            }
            v[j + 1] = tmp;                 // encaixa
            movimentacoes++;
        }
    }

    @Override public String getNome() { return "Insercao"; }
}


/* ---------------------------------------------------------------------
 * MERGESORT (Intercalação)
 * O que faz: divide o vetor ao meio, ordena cada metade (recursão)
 *            e INTERCALA as duas metades ordenadas.
 * Custo: O(n log n) SEMPRE. Estável. Usa vetor auxiliar (memória O(n)).
 * Quando usar: vetor grande + precisa ser estável; ou quando pedirem garantia n log n.
 * ------------------------------------------------------------------- */
class Mergesort<T> extends Ordenador<T> {
    public Mergesort(Comparator<? super T> c) { super(c); }

    @Override
    public void ordenar(T[] v) {
        if (v.length < 2) return;
        T[] aux = v.clone();                // vetor auxiliar
        mergesort(v, aux, 0, v.length - 1);
    }

    private void mergesort(T[] v, T[] aux, int esq, int dir) {
        if (esq >= dir) return;             // 1 elemento: já ordenado
        int meio = (esq + dir) / 2;
        mergesort(v, aux, esq, meio);       // ordena esquerda
        mergesort(v, aux, meio + 1, dir);   // ordena direita
        intercalar(v, aux, esq, meio, dir); // junta as duas
    }

    /** Junta v[esq..meio] e v[meio+1..dir] (já ordenados) em ordem. */
    private void intercalar(T[] v, T[] aux, int esq, int meio, int dir) {
        for (int k = esq; k <= dir; k++) { aux[k] = v[k]; movimentacoes++; }
        int i = esq, j = meio + 1;
        for (int k = esq; k <= dir; k++) {
            if (i > meio)                          v[k] = aux[j++]; // esquerda acabou
            else if (j > dir)                      v[k] = aux[i++]; // direita acabou
            else if (comparar(aux[j], aux[i]) < 0) v[k] = aux[j++]; // direita menor
            else                                   v[k] = aux[i++]; // empate -> esquerda (estável)
            movimentacoes++;
        }
    }

    @Override public String getNome() { return "Mergesort"; }
}


/* ---------------------------------------------------------------------
 * HEAPSORT
 * O que faz: transforma o vetor num HEAP DE MÁXIMO (o maior fica na raiz, v[0]),
 *            troca a raiz com o último e "conserta" o heap, repetindo.
 *            Filhos de i: 2i+1 e 2i+2 (índice começando em 0).
 * Custo: O(n log n) sempre. Não estável. Sem memória extra.
 * Quando usar: vetor grande, pouca memória, e estabilidade não importa.
 * ------------------------------------------------------------------- */
class Heapsort<T> extends Ordenador<T> {
    public Heapsort(Comparator<? super T> c) { super(c); }

    @Override
    public void ordenar(T[] v) {
        int n = v.length;
        for (int i = n / 2 - 1; i >= 0; i--) descer(v, i, n);  // 1) monta o heap
        for (int fim = n - 1; fim > 0; fim--) {                 // 2) tira o maior
            trocar(v, 0, fim);
            descer(v, 0, fim);
        }
    }

    /** Desce o elemento da posição i até o lugar certo no heap (só até 'tam'). */
    private void descer(T[] v, int i, int tam) {
        while (2 * i + 1 < tam) {
            int filho = 2 * i + 1;
            if (filho + 1 < tam && comparar(v[filho + 1], v[filho]) > 0) filho++; // maior filho
            if (comparar(v[i], v[filho]) >= 0) break;                            // já está ok
            trocar(v, i, filho);
            i = filho;
        }
    }

    @Override public String getNome() { return "Heapsort"; }
}


/* ---------------------------------------------------------------------
 * QUICKSORT
 * O que faz: escolhe um PIVÔ (elemento do meio), joga os menores pra esquerda
 *            e os maiores pra direita, e repete em cada lado (recursão).
 * Custo: O(n log n) em média (o mais rápido na prática); O(n²) no pior caso.
 *        Não estável.
 * Quando usar: padrão pra vetor grande quando estabilidade não importa.
 * ------------------------------------------------------------------- */
class Quicksort<T> extends Ordenador<T> {
    public Quicksort(Comparator<? super T> c) { super(c); }

    @Override
    public void ordenar(T[] v) {
        if (v.length > 1) quicksort(v, 0, v.length - 1);
    }

    private void quicksort(T[] v, int esq, int dir) {
        int i = esq, j = dir;
        T pivo = v[(esq + dir) / 2];
        while (i <= j) {
            while (comparar(v[i], pivo) < 0) i++;   // acha alguém >= pivô na esquerda
            while (comparar(v[j], pivo) > 0) j--;   // acha alguém <= pivô na direita
            if (i <= j) { trocar(v, i, j); i++; j--; }
        }
        if (esq < j) quicksort(v, esq, j);
        if (i < dir) quicksort(v, i, dir);
    }

    @Override public String getNome() { return "Quicksort"; }
}


/* ---------------------------------------------------------------------
 * FABRICA DE ORDENADORES
 * O que é: cria o método a partir de um número.
 * Quando usar: quando tiver MENU ("escolha o método de ordenação").
 *   Ordenador<Produto> o = FabricaOrdenador.criar(opcao, Produto.POR_NOME);
 * ------------------------------------------------------------------- */
class FabricaOrdenador {
    public static final String MENU =
            "1-Selecao  2-Bolha  3-Insercao  4-Mergesort  5-Heapsort  6-Quicksort";

    public static <T> Ordenador<T> criar(int opcao, Comparator<? super T> c) {
        switch (opcao) {
            case 1: return new Selecao<>(c);
            case 2: return new Bolha<>(c);
            case 3: return new Insercao<>(c);
            case 4: return new Mergesort<>(c);
            case 5: return new Heapsort<>(c);
            case 6: return new Quicksort<>(c);
            default: throw new IllegalArgumentException("Opcao invalida: " + opcao);
        }
    }
}


/* ---------------------------------------------------------------------
 * LEITOR DE ARQUIVO
 * O que é: lê um arquivo texto/CSV e transforma cada linha em objeto.
 * Quando usar: se o enunciado der um arquivo de dados pra ler.
 *   List<Produto> lista = LeitorArquivo.lerCSV("dados.csv", ";", true, Produto::deCSV);
 *   - separador: ";" ou ","  (se for "|" escreva "\\|")
 *   - true = a 1ª linha é cabeçalho (pula ela)
 * ------------------------------------------------------------------- */
class LeitorArquivo {
    public static <T> List<T> lerCSV(String caminho, String separador, boolean temCabecalho,
                                     Function<String[], T> conversor) throws IOException {
        List<T> lista = new ArrayList<>();
        List<String> linhas = Files.readAllLines(Paths.get(caminho), StandardCharsets.UTF_8);
        for (int i = temCabecalho ? 1 : 0; i < linhas.size(); i++) {
            String linha = linhas.get(i).trim();
            if (!linha.isEmpty()) lista.add(conversor.apply(linha.split(separador)));
        }
        return lista;
    }
}
