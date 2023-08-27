package ch.realmtechServer;

public class ServerHello {
    public static void main(String[] args) {
        new ServerHello().echo();
    }

    public void echo() {
        System.out.println("bonjour du serveur");
    }
}
