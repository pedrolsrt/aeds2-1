import java.util.Comparator;

/*
 * =====================================================================
 *  PRODUTO.JAVA  –  a classe do "sistema". ESTA É A QUE VOCÊ ADAPTA.
 * =====================================================================
 *  Na prova, troque "Produto" pelo que o enunciado pedir (Aluno, Pedido,
 *  Paciente, Funcionario...). Roteiro:
 *    1) Renomeie a classe e o arquivo (no NetBeans: botão direito > Refatorar > Renomear).
 *    2) Troque os atributos, o construtor e os getters.
 *    3) Troque os Comparators (critérios) pelos que o enunciado pedir.
 *    4) Ajuste o toString (como o objeto aparece na tela).
 *    5) Se tiver arquivo pra ler, ajuste o deCSV (ordem das colunas).
 * =====================================================================
 */
public class Produto implements Comparable<Produto> {

    // ---------- ATRIBUTOS: troque pelos do enunciado ----------
    private String nome;
    private double preco;
    private int estoque;

    public Produto(String nome, double preco, int estoque) {
        this.nome = nome;
        this.preco = preco;
        this.estoque = estoque;
    }

    public String getNome()  { return nome; }
    public double getPreco() { return preco; }
    public int getEstoque()  { return estoque; }

    /* ---------------------------------------------------------------
     * compareTo = ORDEM "NATURAL" do objeto (usada quando não se passa critério).
     * Quando usar: se o enunciado pedir "implemente Comparable".
     * ------------------------------------------------------------- */
    @Override
    public int compareTo(Produto outro) {
        return this.nome.compareTo(outro.nome);
    }

    /* ---------------------------------------------------------------
     * COMPARATORS = CRITÉRIOS DE ORDENAÇÃO. Um por atributo pedido.
     * Modelos (copie e troque o nome do atributo):
     *   Texto:   Comparator.comparing(Classe::getAtributo)
     *   Decimal: Comparator.comparingDouble(Classe::getAtributo)
     *   Inteiro: Comparator.comparingInt(Classe::getAtributo)
     *   Decrescente:  CRITERIO.reversed()
     *   Desempate:    CRITERIO1.thenComparing(CRITERIO2)
     * ------------------------------------------------------------- */
    public static final Comparator<Produto> POR_NOME    = Comparator.comparing(Produto::getNome);
    public static final Comparator<Produto> POR_PRECO   = Comparator.comparingDouble(Produto::getPreco);
    public static final Comparator<Produto> POR_ESTOQUE = Comparator.comparingInt(Produto::getEstoque);

    // Ignorando maiúscula/minúscula ("abc" = "ABC")
    public static final Comparator<Produto> POR_NOME_SEM_CASE =
            Comparator.comparing(Produto::getNome, String.CASE_INSENSITIVE_ORDER);

    // Decrescente
    public static final Comparator<Produto> POR_PRECO_DESC = POR_PRECO.reversed();

    // Preço decrescente; se empatar, nome em ordem alfabética
    public static final Comparator<Produto> POR_PRECO_DESC_NOME = POR_PRECO.reversed().thenComparing(POR_NOME);

    /* ---------------------------------------------------------------
     * deCSV: transforma uma linha do arquivo ("Mouse;80,50;30") em Produto.
     * c[0], c[1], c[2] = colunas na ordem do arquivo. Ajuste se mudar.
     * ------------------------------------------------------------- */
    public static Produto deCSV(String[] c) {
        return new Produto(
                c[0].trim(),
                Double.parseDouble(c[1].trim().replace(",", ".")), // aceita 80,50 ou 80.50
                Integer.parseInt(c[2].trim()));
    }

    /** Como o objeto aparece no System.out.println. %-10s = texto com 10 espaços. */
    @Override
    public String toString() {
        return String.format("%-10s R$ %8.2f  estoque: %3d", nome, preco, estoque);
    }
}
