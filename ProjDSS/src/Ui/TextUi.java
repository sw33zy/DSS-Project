package Ui;

import Business.GestArmazemFacade;
import Business.IGestArmazemFacade;
import Business.Palete;
import Business.Posicao;
import Exceptions.PaleteInexistenteException;

import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class TextUi {
    private final IGestArmazemFacade modelo;
    private final Scanner scin;

    public TextUi() {
        this.modelo = new GestArmazemFacade();
        this.scin = new Scanner(System.in);
    }

    public void run() {
        System.out.println("Bem vindo ao Gestor de Armazém!");
        this.menuPrincipal();
        System.out.println("A sair...");
    }

    private void menuPrincipal() {
        Menu menu = new Menu(new String[] {
                "Operações do Leitor.",
                "Operações do Sistema.",
                "Operações do Robot.",
                "Operações do Gestor. "
        });

        menu.setPreCondicao(2, this.modelo::existemPaletesAtransportar);
        menu.setPreCondicao(3,() -> !this.modelo.verificaDisponibilidadeRobot());

        menu.setHandler(1,this::gestorDoLeitor);
        menu.setHandler(2,this::gestorDoSistema);
        menu.setHandler(3,this::gestorDoRobot);
        menu.setHandler(4,this::gestorDoGestor);
        menu.run();
    }

    private void gestorDoLeitor() {
        Menu menu = new Menu(new String[] {"Registar palete."});

        menu.setHandler(1,this::trataRegisto);
        menu.run();
    }

    public void gestorDoSistema() {
        Menu menu = new Menu(new String[] {"Preparar transporte de palete."});

        menu.setHandler(1,this::trataPreparacao);
        menu.run();
    }

    public void gestorDoRobot() {
        Menu menu = new Menu(new String[] {"" +
                "Notificar recolha da palete.",
                "Notificar entrega da palete."
        });

        menu.setHandler(1,this::trataRecolha);
        menu.setHandler(2,this::trataEntrega);
        menu.run();
    }

    public void gestorDoGestor() {
        Menu menu = new Menu(new String[] {"Consultar listagem de localizações ocupadas."});

        menu.setHandler(1,this::trataConsulta);
        menu.run();
    }

    public void trataRegisto() {
        try {
            /* Lê código da palete a registar no sistema */
            System.out.print("Codigo da palete: ");
            String codigo = this.scin.nextLine();

            if (!this.modelo.verificaExistenciaPalete(codigo)) {

                /* Lê altura da palete a registar no sistema */
                System.out.print("Altura da palete: ");
                float altura = this.scin.nextFloat();

                /* Cria registo de palete no sistema e adiciona-a a paletes a transportar*/
                this.modelo.leitorRegisto(codigo,altura);

                System.out.println(" -> Palete registada no sistema com sucesso.");
                System.out.println(" -> Palete adcicionada à lista de paletes a transportar.");
                this.scin.nextLine();

            } else {
                System.out.println(" -> Código de palete já existente!");
            }
            /* Exibe todas as paletes registadas atualmente no sistema */
            this.modelo.mostra();
            /* Exibe o robot existente no sistema */
            this.modelo.mostraRobot();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void trataPreparacao() {
        String palete;
        try {
            palete = this.modelo.iniciaTransportePalete();

            if (palete==null) {
                System.out.println(" -> O robot está indisponível!");
            } else {
                System.out.println(" -> Robot notificado! ");
                System.out.println(" -> Palete selecionada: " + palete);
            }
            /* Exibe todas as paletes registadas atualmente no sistema */
            this.modelo.mostra();
            /* Exibe o robot existente no sistema */
            this.modelo.mostraRobot();

        } catch (PaleteInexistenteException e) {
            System.out.println("Palete não existe!");
        }
    }

    public void trataRecolha() {
        try {

            /* see this case here */
            switch (this.modelo.transportaPalete()) {
                case -2 -> System.out.println(" -> O armazem esta cheio!");
                case -1 -> System.out.println(" -> O robot não tem paletes a recolher!");
                case 0  -> System.out.println(" -> Codigo introduzido não referencia palete à espera de transporte!");
                case 1  -> System.out.println(" -> Palete recolhida e transportada com sucesso!");
            }
            /* Exibe todas as paletes registadas atualmente no sistema */
            this.modelo.mostra();
            /* Exibe o robot existente no sistema */
            this.modelo.mostraRobot();

        } catch (PaleteInexistenteException e) {
            System.out.println("Palete não existe!");
        }
    }

    public void trataEntrega() {
        try {

            switch (this.modelo.concluiTransportePalete()) {
                case -1 -> System.out.println(" -> O robot não tem paletes a entregar!");
                case 0  -> System.out.println(" -> Codigo introduzido nao referencia palete em transporte!");
                case 1  -> System.out.println(" -> Palete entregue!");
            }

            /* Exibe todas as paletes registadas atualmente no sistema */
            this.modelo.mostra();
            /* Exibe o robot existente no sistema */
            this.modelo.mostraRobot();


        } catch (PaleteInexistenteException e) {
            System.out.println("Palete não existe!");
        }
    }

    public void trataConsulta() {
        try {
            Map<String,Posicao> res = this.modelo.consultaPrateleiras();
            if (res.size()>0) {
                for (Map.Entry<String,Posicao> p : res.entrySet()) {
                    if(p.getValue().getNumero()==0)
                        System.out.println("Palete: " + p.getKey() + "-> Zona de Receção");
                    if(p.getValue().getNumero()==99)
                        System.out.println("Palete: " + p.getKey() + "-> Robot");
                    else if (p.getValue().getNumero()!=0 && p.getValue().getNumero()!=99)
                        System.out.println("Palete: " + p.getKey() + "-> Corredor: " + p.getValue().getNumero() + "-> Prateleira: " + (p.getValue().getSeccao()+1));
                }
            } else {
                System.out.println(" -> Nenhuma prateleira ocupada!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
