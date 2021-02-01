package Business;

import Exceptions.PaleteInexistenteException;

import java.util.Map;

public interface IGestArmazemFacade {

    Map<String,Posicao> consultaPrateleiras();

    boolean verificaExistenciaPalete(String codigo) ;

    void leitorRegisto(String codigo, float altura);

    String iniciaTransportePalete() throws PaleteInexistenteException;

    int transportaPalete() throws PaleteInexistenteException;

    int concluiTransportePalete() throws PaleteInexistenteException;

    boolean existemPaletesAtransportar();

    boolean verificaDisponibilidadeRobot();

    void mostra();

    void mostraRobot();

}