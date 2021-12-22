package Client.GUI;

public class Form {

    public static Login login;
    public static Main main;
    public static Chat chat;

    public static void showLogin() {
        if (login == null) {
            login = new Login();
        }
        login.setVisible(true);
    }

    public static void hideLogin() {
        if (login != null) {
            login.setVisible(false);
        }
    }

    public static void showMain() {
        if (main == null) {
            main = new Main();
        }
        main.setVisible(true);
    }

    public static void newMain() {
        main = new Main();
        main.setVisible(true);
    }

    public static void hideMain() {
        if (main != null) {
            main.setVisible(false);
        }
    }

    public static void showChat() {
        if (chat == null) {
            chat = new Chat();
        }
        chat.setVisible(true);
    }

    public static void hideChat() {
        if (chat != null) {
            chat.setVisible(false);
        }
    }

    public static void newChat() {
        chat = new Chat();
        chat.setVisible(true);
    }

}
