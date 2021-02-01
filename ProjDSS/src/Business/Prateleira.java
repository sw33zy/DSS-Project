package Business;

public class Prateleira {
    private final String codigo;
    private final float altura;
    private boolean disponivel;

    /**
     * Construtor da classe Prateleira. Cria uma prateleira dadas as suas características
     * @param codigo código da prateleira
     * @param altura altura da prateleira
     * @param disponivel disponibilidade da prateleira para serem lá armazenadas paletes
     */
    public Prateleira(String codigo,float altura,boolean disponivel) {
        this.codigo = codigo;
        this.altura = altura;
        this.disponivel = disponivel;
    }

    /**
     * Devolve o código da prateleira
     * @return código da prateleira
     */
    public String getCodigo() {
        return codigo;
    }

    /**
     * Devolve a altura da prateleira
     * @return altura da prateleira
     */
    public float getAltura() {
        return altura;
    }

    /**
     * Verifica se a prateleira está disponível
     * @return disponibilidade da prateleira
     */
    public boolean getDisponivel() {
        return disponivel;
    }

    /**
     * Altera a disponibilidade da prateleira
     * @param disponivel estado atual da ocupação da prateleira
     */
    public void setDisponivel(boolean disponivel) {
        this.disponivel = disponivel;
    }

    public Prateleira clone() {
        return new Prateleira(this.codigo,this.altura,this.disponivel);
    }
}
