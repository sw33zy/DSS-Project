import Ui.TextUi;

public class Main {
    public static void main(String[] args) {
        try {
            new TextUi().run();
        } catch (Exception e) {
            System.out.println("Erro de iniciação!");
            e.printStackTrace();
        }
    }
}
