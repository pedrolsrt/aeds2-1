# AEDS 2 – Ordenação Polimórfica (Java)

| Arquivo | O que é | Mexer na prova? |
|---|---|---|
| `Ordenacao.java` | Interface, classe abstrata, 6 métodos, fábrica e leitor de arquivo | Não |
| `Produto.java` | Classe de exemplo + critérios (Comparators) | **Sim** – vira a classe do enunciado |
| `Main.java` | 7 cenários prontos (básico, polimorfismo, critérios, lista, arquivo, desempenho, menu) | Copiar o cenário parecido |

| Método    | Melhor     | Médio      | Pior       | Estável | Memória extra |
|-----------|------------|------------|------------|---------|---------------|
| Seleção   | O(n²)      | O(n²)      | O(n²)      | Não     | O(1)          |
| Bolha     | O(n)       | O(n²)      | O(n²)      | Sim     | O(1)          |
| Inserção  | O(n)       | O(n²)      | O(n²)      | Sim     | O(1)          |
| Mergesort | O(n log n) | O(n log n) | O(n log n) | Sim     | O(n)          |
| Heapsort  | O(n log n) | O(n log n) | O(n log n) | Não     | O(1)          |
| Quicksort | O(n log n) | O(n log n) | O(n²)      | Não     | O(log n)      |
