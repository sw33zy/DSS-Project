package Ui;

import java.util.*;

public class Menu {
    public interface MenuHandler {
        void executa();
    }

    public interface MenuPreCondicao {
        boolean valida();
    }

    private static final Scanner is = new Scanner(System.in);

    private final List<String> opcoes;
    private final List<MenuPreCondicao> disponivel;
    private final List<MenuHandler> handlers;

    public Menu(String[] opcoes) {

        this.opcoes = Arrays.asList(opcoes);
        this.disponivel = new ArrayList<>();
        this.handlers = new ArrayList<>();

        this.opcoes.forEach(s -> {
            this.disponivel.add(() -> true); // preCondiciona todas as opções com true
            this.handlers.add(() -> System.out.println("Ainda não está implementada!"));
        });
    }

    public void run() {
        int opcao;

        do {
            mostra();
            opcao = lerOpcao();
            if (opcao > 0 && !this.disponivel.get(opcao - 1).valida()) {
                System.out.println("Opção indisponiíel!");
            } else if (opcao > 0) {
                this.handlers.get(opcao - 1).executa();
            }
        } while (opcao != 0);
    }

    public void setPreCondicao(int opcao,MenuPreCondicao m) {
        this.disponivel.set(opcao - 1,m);
    }

    public void setHandler(int opcao,MenuHandler m) {
        this.handlers.set(opcao - 1,m);
    }

    private void mostra() {
        System.out.println("\n########### Menu ###########");

        for (int i = 0; i<this.opcoes.size(); i++) {
            System.out.print(i+1);
            System.out.print(" - ");
            System.out.println(this.disponivel.get(i).valida() ? this.opcoes.get(i) : "----------");
        }
        System.out.println("0 - Sair");
    }

    private int lerOpcao() {
        int opcao;
        System.out.print("Opção: ");

        try {
            String linha = is.nextLine();
            opcao = Integer.parseInt(linha);

        } catch (NumberFormatException e) {
            opcao = -1;
        }

        if (opcao < 0 || opcao > this.opcoes.size()) {
            System.out.println("Opção invalida!!");
            opcao = -1;
        }
        return opcao;
    }
}
