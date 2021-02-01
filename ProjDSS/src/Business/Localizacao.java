package Business;

public abstract class Localizacao implements Cloneable {

    private int numero;

    /**
     * Construtor da classe abstrata Localizacao. Cria uma localização com um determinado número identificador
     * @param numero número identificador
     */
    protected Localizacao(int numero) {

        this.numero = numero;
    }

    /**
     * Devolve o número identificador da localização
     * @return número da localização
     */
    public int getNumero() {

        return numero;
    }

    /**
     * Substitui o número identificador da localização por outro fornecido
     * @param numero novo número da localização
     */
    public void setNumero(int numero) {
        this.numero = numero;
    }


    public Localizacao clone(){
        try {
            return (Localizacao) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    public abstract String toString();

}
