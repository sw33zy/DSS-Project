package Business;

import java.util.Queue;

public class Robot {
    private final String codigo;
    private boolean disponivel;
    private String palete;
    private boolean recolheuPalete;
    private Percurso percurso;
    private Localizacao loc;

    /**
     * Construtor da classe Robot. Cria um robot com as características dadas
     * @param codigo código do robot
     * @param disponivel disponibilidade do robot para fazer um transporte
     * @param palete palete que o robot tem de transportar, caso haja, senão null
     * @param percurso percurso que o robot tem de percorrer para transportar a palete, caso haja, senão null
     * @param loc localização atual do robot
     */
    public Robot(String codigo, boolean disponivel,String palete,Percurso percurso,Localizacao loc) {
        this.codigo = codigo;
        this.disponivel = disponivel;
        this.palete = palete;
        this.recolheuPalete = false;
        this.percurso = percurso;
        this.loc = loc;
    }

    /**
     * Verifica se o robot está disponível para fazer um transporte
     * @return indica a dispnibilidade do robot
     */
    public boolean getDisponivel() {
        return disponivel;
    }

    /**
     * Altera a disponibilidade do robot
     * @param disponivel estado da disponibilidade do robot
     */
    public void setDisponivel(boolean disponivel) {
        this.disponivel = disponivel;
    }

    /**
     * Devolve a palete que o robot tem de transportar
     * @return palete que o robot tem de transportar, caso haja, senão null
     */
    public String getPalete() {
        return palete;
    }

    /**
     * Atribui uma palete ao robot para ele a transportar
     * @param palete palete que o robot tem de transportar
     */
    public void setPalete(String palete) {
        this.palete = palete;
    }

    /**
     * Atualiza o estado do robot ter recolhido a palete
     * @param state estado de recolha de palete
     */
    public void setRecolheuPalete(boolean state) {
        this.recolheuPalete = state;
    }

    /**
     * Verifica se o robot recolheu a palete
     * @return indica se o robot recolheu a palete
     */
    public boolean getRecolheuPalete() {
        return this.recolheuPalete;
    }

    /**
     * Atribui um percurso ao robot
     * @param percuso percurso que o robot terá de percorrer
     */
    public void setPercuso(Percurso percuso) {
        this.percurso = percuso;
    }

    /**
     * Devolve a localização atual do robot
     * @return localização do robot
     */
    public Localizacao getLoc() {
        return loc;
    }

    /**
     * Movimenta o robot pelo percurso que tem de percorrer para transportar ou recolher uma palete
     */
    public void movimenta() {

        Queue<Localizacao> p = this.percurso.getPercurso();

        while (p.size()>0) {
            this.loc = p.remove();
            System.out.println("A percorrer -> " + this.loc.toString());
            try {
                Thread.sleep(1000);
            } catch (Exception ignored) {}
        }
    }

    @Override
    public String toString() {
        return "Robot { "
                + "código = '" + codigo + '\''
                + ", disponivel = " + disponivel
                + ", palete = '" + palete + '\''
                + ", percuso = " + percurso
                + ", localização = " + loc
                + '}';
    }
}
