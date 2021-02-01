package Business;

public class CorredorSemArmazenamento extends Localizacao {

    /**
     * Construtor da classe CorredorSemArmazenamento. Cria um corredor sem armazenamento.
     * @param numero número do corredor a criar
     */
    public CorredorSemArmazenamento(int numero) {
        super(numero);
    }

    @Override
    public String toString() {
        return "Número: " + this.getNumero();
    }


}
