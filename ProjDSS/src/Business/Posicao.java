package Business;

public class Posicao extends Localizacao {
    int seccao;

    /**
     * Construtor da classe Posicao. Cria uma posição no mapa
     */
    public Posicao() {
        super(0);
        this.seccao = 0;
    }

    /**
     * Construtor da classe Posicao. Cria uma posição no mapa dado o seu número identificador e a sua secção
     * @param n número identificador
     * @param s secção
     */
    public Posicao(int n,int s) {
        super(n);
        this.seccao = s;
    }

    /**
     * Construtor da classe Posicao. Cria uma posição com base noutra dada
     * @param p
     */
    public Posicao(Posicao p) {
        super(p.getNumero());
        this.seccao = p.getSeccao();
    }

    /**
     * Devolve a sua secção
     * @return secção
     */
    public int getSeccao() {
        return seccao;
    }

    public Posicao clone() {return new Posicao(this); }

    @Override
    public String toString() {
        return "Número: " + this.getNumero() + " Posição { "
                + "secção = " + seccao + '}';
    }
}
