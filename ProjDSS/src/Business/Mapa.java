package Business;

import Data.LocalizacaoDAO;
import Data.PrateleirasDAO;

import java.util.*;

public class Mapa {
    private LocalizacaoDAO corredores;
    private Map<Integer,Integer> mapaArmazenamento;
    private PrateleirasDAO prateleiras;
    private int totalPrateleiras;

    /**
     * Cria o mapa que conecta os corredores
     */
    private void criaMapaArmazenamento() {
        this.mapaArmazenamento.put(1,0);
        this.mapaArmazenamento.put(2,0);
        this.mapaArmazenamento.put(3,0);
        this.mapaArmazenamento.put(4,1);
        this.mapaArmazenamento.put(5,2);
        this.mapaArmazenamento.put(6,3);
        this.mapaArmazenamento.put(9,4);
        this.mapaArmazenamento.put(8,5);
        this.mapaArmazenamento.put(7,6);
    }

    /**
     * Cria prateleiras no mapa
     * @param x número de prateleiras a criar
     * @return Map com as prateleiras criadas identificadas pelo respetivo código
     */
    private Map<String,Prateleira> criaPrateleiras(int x) {

        Map<String,Prateleira> res = new HashMap<>();

        for (int i = 0; i<x; i++) {
            String s = "P"+totalPrateleiras++;
            res.put(s,new Prateleira(s,(i+1)*10,true));
            this.prateleiras.put(s,new Prateleira(s,(i+1)*10,true));
        }
        return res;
    }

    /**
     * Cria corredores com e sem armazenamento
     */
    private void criaCorredores() {
        if(this.corredores.size()==0) {
            this.corredores.put(0, new Posicao(0, 0));
            this.corredores.put(1, new CorredorSemArmazenamento(1));
            this.corredores.put(2, new CorredorSemArmazenamento(2));
            this.corredores.put(3, new CorredorSemArmazenamento(3));
            this.corredores.put(4, new CorredorComArmazenamento(4, criaPrateleiras(5)));
            this.corredores.put(5, new CorredorComArmazenamento(5, criaPrateleiras(5)));
            this.corredores.put(6, new CorredorComArmazenamento(6, criaPrateleiras(5)));
            this.corredores.put(7, new CorredorSemArmazenamento(7));
            this.corredores.put(8, new CorredorSemArmazenamento(8));
            this.corredores.put(9, new CorredorSemArmazenamento(9));
            this.corredores.put(10, new Posicao(10, 0));
        }
    }

    /**
     * Construtor da classe Mapa. Cria um mapa com os dados dos corredores e prateleiras armazenados na base de dados
     */
    public Mapa() {
        this.mapaArmazenamento = new HashMap<>();
        this.corredores = LocalizacaoDAO.getInstance();
        this.prateleiras = PrateleirasDAO.getInstance();
        this.totalPrateleiras = 1;
        this.criaMapaArmazenamento();
        this.criaCorredores();
    }

    /**
     * Reserva uma prateleira para ser colocada uma palete
     * @param codigo código da prateleira compatível com a palete que se pretende guardar
     * @return posição no mapa onde vai ser guardada a palete
     */
    public Localizacao reservaPrateleiraPorCodigo(String codigo) {

        for (Map.Entry<Integer,Localizacao> m : this.corredores.entrySetL(11)) {
            Localizacao l = m.getValue();

            if (l instanceof CorredorComArmazenamento) {
                CorredorComArmazenamento c = (CorredorComArmazenamento) l;

                if (c.existePrateleira(codigo)) {

                    int seccao = c.reservaPorCodigo(codigo);

                    if (seccao != -1 && seccao != -2) {
                        updatePrateleiras(codigo);

                        return new Posicao(c.getNumero(), seccao);
                    }
                }
            }
        }
        return null;
    }

    /**
     * Coloca a prateleira a indisponível quando é feita a sua reserva para armazenar uma palete
     * @param codigo código da prateleira
     */
    public void updatePrateleiras(String codigo) {
        Prateleira ptl = null;

        for (Localizacao m : this.corredores.values())

            if (m instanceof CorredorComArmazenamento)

                if (((CorredorComArmazenamento) m).existePrateleira(codigo)) {
                    ptl = ((CorredorComArmazenamento) m).getPrateleira(codigo);
                    ptl.setDisponivel(false);
                    break;
                }

        this.prateleiras.put(codigo,ptl);
    }

    /**
     * Procura uma prateleira com uma determinada altura
     * @param altura altura pretendida
     * @return código de uma prateleira livre com a altura fornecida
     */
    public String procuraPrateleira(float altura) {
        return this.prateleiras.prateleiraLivre(altura);
    }

    /**
     * Calcula o percurso para a recolha de uma palete
     * @param i localização inicial, do robot
     * @param f localização final, onde se encontra a palete
     * @return localizações que o robot terá de percorrer para chegar da origem ao destino
     */
    private Queue<Localizacao> calculaPercursoRecolha(Localizacao i,Localizacao f) {

        Queue<Localizacao> res = new ArrayDeque<>();
        int o = i.getNumero();
        int d = f.getNumero();
        int corredorAnterior;

        res.add(new Posicao(o,0));

        while (o!=d) {
            corredorAnterior = this.mapaArmazenamento.get(o);
            res.add(new Posicao(corredorAnterior,0));
            o = corredorAnterior;
        }

        return res;
    }

    /**
     * Calcula o percurso para o armazenamento de uma palete
     * @param i localizaçao inicial, do robot
     * @param f localização final, prateleira onde será guardada a palete
     * @return localizações que o robot terá de percorrer para chegar da origem ao destino
     */
    private Queue<Localizacao> calculaPercursoArmazenamento(Localizacao i, Localizacao f) {

        List<Localizacao> res = new ArrayList<>();
        Queue<Localizacao> inversa = new ArrayDeque<>();
        int o = i.getNumero();
        int d = f.getNumero();
        int corredorAnterior;

        res.add(f);

        while (o!=d) {
            corredorAnterior = this.mapaArmazenamento.get(d);
            res.add(new Posicao(corredorAnterior,0));
            d = corredorAnterior;
        }

        for (int k = res.size(); k>0; k--)
            inversa.add(res.get(k-1));

        return inversa;
    }

    /**
     * Calcula o percurso para o robot percorrer
     * @param i localização inicial
     * @param f localização final
     * @return percurso que o robot terá de percorrer para chegar da origem ao destino
     */
    public Percurso calculaPercurso(Localizacao i,Localizacao f) {

        Queue<Localizacao> res = new ArrayDeque<>();
        int o = i.getNumero();
        int d = f.getNumero();

        if (o > d)
            res = calculaPercursoRecolha(i,f);

        else if (o < d)
            res = calculaPercursoArmazenamento(i,f);

        return new Percurso(res);
    }
}
