import chess.*;
import serverfacade.ServerFacade;
import ui.PreLoginUI;

public class Main {
    public static void main(String[] args) {
        System.out.println("♕ Welcome to Hailey's Chess Server. Type help for a list of commands");
        try{
            ServerFacade facade = new ServerFacade(8080);
            new PreLoginUI(facade).run();

        } catch (Exception ex) {
            System.out.printf("Unable to start server: %s%n", ex.getMessage());
        }
    }
}