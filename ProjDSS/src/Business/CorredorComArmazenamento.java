package Business;

import java.util.*;

public class CorredorComArmazenamento extends Localizacao {

    private Map<String, Prateleira> prateleira;
    private final Map<Integer, String> posicaoDaPrateleira;

    /**
     * Construtor da classe CorredorComArmazenamento. Cria um corredor com armazenamento.
     * @param numero número do corredor a criar
     * @param prateleira prateleiras a incluir no corredor
     */
    public CorredorComArmazenamento(int numero, Map<String, Prateleira> prateleira) {
        super(numero);

        setPrateleira(prateleira);

        this.posicaoDaPrateleira = new HashMap<>();
        int cont = 0;

        for (String s : this.prateleira.keySet())
            this.posicaoDaPrateleira.put(cont++, s);
    }

    /**
     * Substitui as prateleiras do corredor com as recebidas.
     * @param prateleira prateleiras substitutas
     */
    private void setPrateleira(Map<String, Prateleira> prateleira) {

        this.prateleira = new HashMap<>();

        for (Map.Entry<String, Prateleira> m : prateleira.entrySet())
            this.prateleira.put(m.getKey(), m.getValue());
    }

    /**
     * Verifica se existe uma determinada prateleira no corredor.
     * @param codigo código da prateleira
     * @return indica se existe ou não a prateleira no corredor
     */
    public boolean existePrateleira(String codigo) {
        return this.prateleira.get(codigo) != null;
    }

    /**
     * Devolve uma determinada prateleira, dado o código
     * @param codigo código da prateleira pretendida
     * @return prateleira pretendida, caso exista, ou null, caso não exista no corredor
     */
    public Prateleira getPrateleira(String codigo) {
        return this.prateleira.get(codigo);
    }

    /**
     * Reserva uma determinada prateleira para guardar uma palete
     * @param codigo código da prateleira
     * @return número da secção onde se encontra a prateleira
     */
    public int reservaPorCodigo(String codigo) {
        int x = -1;
        if (!this.prateleira.get(codigo).getDisponivel()) return -2;

        this.prateleira.get(codigo).setDisponivel(false);

        for (Map.Entry<Integer, String> m : this.posicaoDaPrateleira.entrySet())
            if (m.getValue().equals(codigo)) {
                x = m.getKey();
                break;
            }

        return x;
    }

    /**
     * Calcula o número de prateleiras existentes no corredor
     * @return número de prateleiras do corredor
     */
    public int nrPrateleiras() {
        return this.prateleira.size();
    }

    @Override
    public String toString() {
        return "Número: " + this.getNumero()
                + "\nCorredorComArmazenamento { "
                + "prateleira = " + prateleira
                + ", posicaoDaPrateleira = " + posicaoDaPrateleira +
                '}';
    }
}