package uwu.misaka;

import javax.swing.*;

public class LoginScreen extends JFrame {
    JTextField login;
    JPasswordField password;
    JPanel panel;
    JButton logIn;

    public LoginScreen(){
        super("SKATCOINMINER|ВХОД В КЛИЕНТ");
        this.setSize(150, 300);
        login = new JTextField();
        password = new JPasswordField();
        logIn = new JButton("Братик я вхожу");
        logIn.addActionListener(e -> {
            //отправка даты
        });
    }
}
