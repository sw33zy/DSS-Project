package Business;

public class Palete implements Cloneable {
    private final String codigo;
    private final float altura;
    private Localizacao loc;
    private boolean disponivel;
    private boolean emTransporte;

    /**
     * Construtor da classe Palete. Cria uma palete
     * @param codigo código da palete
     * @param altura altura da palete
     * @param loc localização da palete
     * @param disponivel disponibilidade da palete para ser requisitada
     * @param emTransporte indica se a palete está neste momento a ser transportada
     */
    public Palete(String codigo, float altura, Localizacao loc, boolean disponivel,boolean emTransporte) {
        this.codigo = codigo;
        this.altura = altura;
        this.loc = loc;
        this.disponivel = disponivel;
        this.emTransporte = emTransporte;
    }

    /**
     * Devolve a disponibilidade da palete
     * @return indica se a palete está disponível
     */
    public boolean getDisponivel() {
        return disponivel;
    }

    /**
     * Devolve a altura da palete
     * @return altura da palete
     */
    public float getAltura() {
        return altura;
    }

    /**
     * Devolve o código da palete
     * @return código da palete
     */
    public String getCodigo() {
        return codigo;
    }

    /**
     * Deveolve a localização da palete
     * @return localização da palete
     */
    public Localizacao getLoc() {
        return loc;
    }

    /**
     * Atribui uma localização à palete
     * @param loc localização nova
     */
    public void setLoc(Localizacao loc) {
        this.loc = loc;
    }

    /**
     * Verifica se a palete está ou não a ser transportada
     * @return indica se a palete está a ser transportada
     */
    public boolean getEmTransporte() {
        return emTransporte;
    }

    /**
     * Atualiza a situação de transporte da palete
     * @param emTransporte indica se a palete está ou não em transporte
     */
    public void setEmTransporte(boolean emTransporte) {
        this.emTransporte = emTransporte;
    }

    /**
     * Atualiza o estado da palete quando o seu transporte é concluído
     * @param loc localização atual da palete
     */
    public void setTransporteConcluido(Localizacao loc) {
        this.loc = loc;
        this.disponivel = true;
        this.emTransporte = false;
    }

    @Override
    public Palete clone() {
        try {
            return (Palete) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    @Override
    public String toString() {
        return "Palete{" +
                "codigo='" + codigo + '\'' +
                ", altura=" + altura +
                ", loc=" + loc +
                ", disponivel=" + disponivel +
                ", emTransporte=" + emTransporte +
                '}';
    }

}
