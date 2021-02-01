package Business;

public class Leitor {

    /**
     * Construtor da classe Leitor. Cria um leitor de códigos
     */
    public Leitor() {}

    /**
     * Cria uma nova palete
     * @param codigo código da nova palete
     * @param altura altura da nova palete
     * @return nova palete com o código e alura fornecidos
     */
    public Palete regista(String codigo,Float altura) {

        return new Palete(codigo,altura,new Posicao(),false,false);
    }
}
