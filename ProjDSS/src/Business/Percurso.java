package Business;

import java.util.ArrayDeque;
import java.util.Queue;

public class Percurso {
    private Queue<Localizacao> percurso;

    /**
     * Construtor da classe Percurso. Cria um percurso dadas as localizações por que ele passa
     * @param percurso conjunto das localizações por onde o percurso tem de passar
     */
    public Percurso(Queue<Localizacao> percurso) {
        this.percurso = percurso;
    }

    /**
     * Construtor da classe Percurso. Cria um percurso dado outro
     * @param p percurso a ser atribuído ao atual
     */
    public Percurso(Percurso p) {
        setPercurso(p.getPercurso());
    }

    /**
     * Devolve o conjunto das localizações que compõem o percurso
     * @return localizações que constituem o percurso
     */
    public Queue<Localizacao> getPercurso() {
        Queue<Localizacao> aux = new ArrayDeque<>();

        for(Localizacao l : this.percurso)
            aux.add(l.clone());

        return aux;
    }

    /**
     * Substitui o percurso atual por outro dado
     * @param percurso conjunto das localizações para substituir
     */
    public void setPercurso(Queue<Localizacao> percurso) {
        this.percurso = new ArrayDeque<>();

        for(Localizacao l : percurso)
            this.percurso.add(l.clone());
    }

    @Override
    public Percurso clone() {
        return new Percurso(this);
    }

    @Override
    public String toString() {
        return "Percurso { " + percurso + " }";
    }
}
