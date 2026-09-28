import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/*
 * =====================================================================
 *  EXEMPLOCOMPLETO.JAVA  –  UM PROGRAMA INTEIRO, DO INÍCIO AO FIM.
 * =====================================================================
 *  Simula um enunciado típico de "Ordenação Polimórfica em Sistemas de Software":
 *
 *   "Um sistema acadêmico precisa ordenar alunos por diferentes critérios.
 *    Crie a classe Aluno (nome, matrícula, curso, nota). Usando polimorfismo,
 *    ordene: (a) por nome; (b) por nota decrescente, desempate por nome;
 *    (c) por curso e, dentro do curso, por nota decrescente.
 *    Compare todos os métodos de ordenação mostrando comparações,
 *    movimentações e tempo."
 *
 *  PRECISA DE: Ordenacao.java na mesma pasta (o motor).
 *
 *  NA PROVA: troque "Aluno" pela classe do enunciado (Ctrl+H no VS Code),
 *  troque os atributos (PARTE 1), os critérios (PARTE 2), os dados (PARTE 3)
 *  e apague das PARTES 4 a 6 o que o enunciado não pedir.
 *
 *  RODAR:  javac -d out *.java   depois   java -cp out ExemploCompleto
 * =====================================================================
 */


/* =====================================================================
 * PARTE 1 – A CLASSE DO SISTEMA (troque pelos atributos do enunciado)
 * ===================================================================== */
class Aluno implements Comparable<Aluno> {

    private String nome;
    private int matricula;
    private String curso;
    private double nota;

    public Aluno(String nome, int matricula, String curso, double nota) {
        this.nome = nome;
        this.matricula = matricula;
        this.curso = curso;
        this.nota = nota;
    }

    public String getNome()    { return nome; }
    public int getMatricula()  { return matricula; }
    public String getCurso()   { return curso; }
    public double getNota()    { return nota; }

    /** Ordem natural (se pedirem Comparable): por matrícula. */
    @Override
    public int compareTo(Aluno outro) {
        return Integer.compare(this.matricula, outro.matricula);
    }

    /* =================================================================
     * PARTE 2 – OS CRITÉRIOS DE ORDENAÇÃO (um por item do enunciado)
     * ================================================================= */
    public static final Comparator<Aluno> POR_NOME      = Comparator.comparing(Aluno::getNome);
    public static final Comparator<Aluno> POR_MATRICULA = Comparator.comparingInt(Aluno::getMatricula);
    public static final Comparator<Aluno> POR_NOTA      = Comparator.comparingDouble(Aluno::getNota);

    // (b) nota decrescente; se empatar, nome A-Z
    public static final Comparator<Aluno> POR_NOTA_DESC_NOME =
            POR_NOTA.reversed().thenComparing(POR_NOME);

    // (c) curso A-Z; dentro do curso, nota decrescente
    public static final Comparator<Aluno> POR_CURSO_NOTA_DESC =
            Comparator.comparing(Aluno::getCurso).thenComparing(POR_NOTA.reversed());

    /** Como o aluno aparece na tela. */
    @Override
    public String toString() {
        return String.format("%-10s mat: %-6d curso: %-12s nota: %5.1f",
                nome, matricula, curso, nota);
    }
}


/* =====================================================================
 * PROGRAMA PRINCIPAL
 * ===================================================================== */
public class ExemploCompleto {

    /* =================================================================
     * PARTE 3 – OS DADOS (troque pelos do enunciado)
     * Método que devolve um vetor NOVO toda vez -> cada ordenação
     * começa do original, sem uma bagunçar a outra.
     * ================================================================= */
    static Aluno[] criarAlunos() {
        return new Aluno[] {
            new Aluno("Carlos",  1045, "Software",   7.5),
            new Aluno("Ana",     1012, "Computacao", 9.0),
            new Aluno("Bruno",   1033, "Software",   9.0),
            new Aluno("Diana",   1001, "Sistemas",   6.0),
            new Aluno("Eduardo", 1050, "Computacao", 7.5),
            new Aluno("Fernanda",1020, "Software",   8.2),
            new Aluno("Gabriel", 1008, "Sistemas",   9.5),
        };
    }

    public static void main(String[] args) {

        System.out.println("===== LISTA ORIGINAL =====");
        imprimir(criarAlunos());

        /* =============================================================
         * PARTE 4 – ORDENAÇÕES PEDIDAS (a), (b), (c)
         * Variável do tipo Ordenador (classe-mãe) = POLIMORFISMO.
         * ============================================================= */

        // (a) por nome – Quicksort
        Aluno[] v = criarAlunos();
        Ordenador<Aluno> ordenador = new Quicksort<>(Aluno.POR_NOME);
        ordenador.executar(v);
        System.out.println("\n===== (a) POR NOME – " + ordenador.getNome() + " =====");
        imprimir(v);
        System.out.println(ordenador);

        // (b) por nota decrescente, desempate por nome – Mergesort (estável)
        v = criarAlunos();
        ordenador = new Mergesort<>(Aluno.POR_NOTA_DESC_NOME);   // mesma variável, outro método
        ordenador.executar(v);
        System.out.println("\n===== (b) POR NOTA DECRESCENTE – " + ordenador.getNome() + " =====");
        imprimir(v);
        System.out.println(ordenador);

        // (c) por curso e nota decrescente – Heapsort
        v = criarAlunos();
        ordenador = new Heapsort<>(Aluno.POR_CURSO_NOTA_DESC);
        ordenador.executar(v);
        System.out.println("\n===== (c) POR CURSO, DEPOIS NOTA – " + ordenador.getNome() + " =====");
        imprimir(v);
        System.out.println(ordenador);

        /* =============================================================
         * PARTE 5 – COMPARAR TODOS OS MÉTODOS (polimorfismo com lista)
         * Mesmo critério, 6 métodos diferentes, mesma chamada executar().
         * ============================================================= */
        System.out.println("\n===== COMPARACAO DOS METODOS (criterio: nota decrescente) =====");
        List<Ordenador<Aluno>> metodos = new ArrayList<>();
        metodos.add(new Selecao<>(Aluno.POR_NOTA_DESC_NOME));
        metodos.add(new Bolha<>(Aluno.POR_NOTA_DESC_NOME));
        metodos.add(new Insercao<>(Aluno.POR_NOTA_DESC_NOME));
        metodos.add(new Mergesort<>(Aluno.POR_NOTA_DESC_NOME));
        metodos.add(new Heapsort<>(Aluno.POR_NOTA_DESC_NOME));
        metodos.add(new Quicksort<>(Aluno.POR_NOTA_DESC_NOME));

        Ordenador<Aluno> melhor = null;
        for (Ordenador<Aluno> m : metodos) {
            Aluno[] copia = criarAlunos();
            m.executar(copia);
            System.out.println(m + "  ordenado=" + m.estaOrdenado(copia));
            if (melhor == null || m.getComparacoes() < melhor.getComparacoes()) melhor = m;
        }
        System.out.println("Menos comparacoes: " + melhor.getNome());

        /* =============================================================
         * PARTE 6 – RESULTADO FINAL ÚNICO (se o enunciado pedir só um)
         * Ex.: "mostre o ranking dos alunos" -> ordena e mostra posição.
         * ============================================================= */
        System.out.println("\n===== RANKING =====");
        v = criarAlunos();
        new Mergesort<>(Aluno.POR_NOTA_DESC_NOME).ordenar(v);
        for (int i = 0; i < v.length; i++) {
            System.out.printf("%dº  %s%n", i + 1, v[i]);
        }
    }

    /** Imprime o vetor, uma linha por elemento. */
    static void imprimir(Aluno[] v) {
        for (Aluno a : v) System.out.println("  " + a);
    }
}
