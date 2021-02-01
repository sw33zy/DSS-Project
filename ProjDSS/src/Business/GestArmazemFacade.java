package Business;

import Data.PaleteDAO;
import Exceptions.PaleteInexistenteException;

import java.util.*;


public class GestArmazemFacade implements IGestArmazemFacade {
    private final Robot robot;
    private PaleteDAO paletes;
    private Queue<String> paletesAtransportar;
    private final Mapa mapa;
    private final Leitor leitor;

    /**
     * Cosntrutor da classe GestArmazemFacade. Cria um gestor de armazém com um robot, um leitor de códigos, um mapa, e com as paletes guardadas na base de dados
     */
    public GestArmazemFacade() {
        this.robot = new Robot("1",true,null,null, new CorredorSemArmazenamento(0));
        this.paletes = PaleteDAO.getInstance();
        this.paletesAtransportar = this.paletes.queueTransporte();
        this.leitor = new Leitor();
        this.mapa = new Mapa();
    }

    /**
     * Verifica se existe alguma palete com determinado código no sistema
     * @param codigo código de palete
     * @return indica se já existe ou não uma palete com o cósigo fornecido
     */
    public boolean verificaExistenciaPalete(String codigo) {
        return !(this.paletes.get(codigo) == null);
    }

    /**
     * Determina uma listagem com a posiçao de cada prateleira, identificada pelo seu código
     * @return Map com a posição de cada prateleira identificada pelo código
     */
    public Map<String,Posicao> consultaPrateleiras() {
        Map<String,Posicao> aux = new HashMap<>();

        for(Map.Entry<String,Posicao> p : this.paletes.listagem().entrySet())
            aux.put(p.getKey(),p.getValue().clone());

        return aux;
    }

    /**
     * Verifica se existem paletes à espera de transporte
     * @return indica se há alguma palete a aguardar transporte
     */
    public boolean existemPaletesAtransportar() {

        return this.paletesAtransportar.size() > 0;
    }

    /**
     * Verifica se o robot está disponível para efetuar um transporte
     * @return indica se o robot está ou não disponível
     */
    public boolean verificaDisponibilidadeRobot() {

        return this.robot.getDisponivel();
    }

    /**
     * Inicia o transporte de uma palete, colocando o robot no lugar onde se encontra a palete a transportar
     * @return palete que vai ser transportada
     */
    public String iniciaTransportePalete() throws PaleteInexistenteException{

        if (this.robot.getDisponivel()) {

            String palete = this.paletesAtransportar.remove();

            Percurso p = this.mapa.calculaPercurso(this.robot.getLoc(), /* origem */
                                                   this.paletes.get(palete).getLoc()); /* destino */

            Palete pal = this.paletes.get(palete);
            if(pal==null){
                throw new PaleteInexistenteException();
            }
            pal.setEmTransporte(true);
            this.paletes.put(palete, pal);

            this.robot.setDisponivel(false);
            this.robot.setPalete(palete);
            this.robot.setPercuso(p.clone());
            this.robot.movimenta();

            return palete;
        }
        return null;
    }

    /**
     * O robot efetua o transporte de uma palete
     * @return devolve -1 caso o robot não tenha uma palete atribuída, -2 caso não haja prateleiras compatíveis, 1 caso se efetue a recolha da palete com sucesso, ou 0 caso a palete não esteja registada no sistema
     */
    public int transportaPalete() throws PaleteInexistenteException{

        String palete = this.robot.getPalete();
        int r = 0;

        if (palete == null) r = -1;

        else {
            if(this.paletes.get(palete)==null){
                throw new PaleteInexistenteException();
            }
            float altura = this.paletes.get(palete).getAltura();

            String codigo = this.mapa.procuraPrateleira(altura);

            if (codigo == null) { /* caso não haja prateleiras disponíveis para a palete em questão */
                this.robot.setDisponivel(true);
                Palete p1 = this.paletes.get(palete);
                p1.setEmTransporte(false);
                this.paletes.put(palete,p1);

                return -2;
            }

            Localizacao dest = this.mapa.reservaPrateleiraPorCodigo(codigo); /* destino */

            Localizacao loc = this.robot.getLoc(); /* origem (o robot já se encontra junto da palete) */

            Percurso p = this.mapa.calculaPercurso(loc,dest);

            Palete pal = this.paletes.get(palete);

            if (pal != null) {
                pal.setLoc(loc);

                this.paletes.put(palete,pal);

                this.robot.setPercuso(p);
                this.robot.movimenta();
                this.robot.setRecolheuPalete(true);

                r = 1;
            }
        }
        return r;
    }

    /**
     * O transporte da palete é concluído
     * @return -1 caso o robot não esteja a transportar nenhuma palete ou ainda não a tenha recolhido, 1 caso o transporte seja efetuado com sucesso
     */
    public int concluiTransportePalete() throws PaleteInexistenteException{

        String palete = this.robot.getPalete();

        if (palete == null || !this.robot.getRecolheuPalete()) return -1;

        else {
            Palete pal = this.paletes.get(palete);
            if(pal==null)
                throw new PaleteInexistenteException();
            pal.setTransporteConcluido(this.robot.getLoc());

            Palete pal2 = pal;

            Posicao l = (Posicao) this.robot.getLoc().clone();
            l.setNumero(Integer.parseInt("" + l.getNumero() + l.getSeccao()));

            pal2.setLoc(l);

            this.paletes.put(palete,pal2);
            this.robot.setDisponivel(true);
            this.robot.setPalete(null);
            this.robot.setRecolheuPalete(false);

            return 1;
        }
    }

    /**
     * É adicionada uma palete ao sistema
     * @param codigo código da nova palete
     * @param altura altura da nova palete
     */
    public void leitorRegisto(String codigo,float altura) {

        Palete p = this.leitor.regista(codigo,altura);

        this.paletes.put(codigo,p.clone());
        this.paletesAtransportar.add(codigo);
    }

    /**
     * Mostra o estado ads paletes existentes no sistema
     */
    public void mostra() {

        for (Palete p : this.paletes.values())
            System.out.println(p.toString());
    }

    /**
     * Mostra o estado do robot
     */
    public void mostraRobot() {

        System.out.println(this.robot.toString());
    }
}
