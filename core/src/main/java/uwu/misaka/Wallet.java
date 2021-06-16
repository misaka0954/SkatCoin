package uwu.misaka;

import java.util.*;

public class Wallet {
    public String id;
    public String login;
    public String password;
    public List<BaseCoin> walletCoins;

    public Wallet(String id, String login, String password) {
        this.id = id;
        this.login = login;
        this.password = password;
        this.walletCoins = new ArrayList<>();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Wallet wallet = (Wallet) o;
        return id.equals(wallet.id) && login.equals(wallet.login) &&
                password.equals(wallet.password) && walletCoins.equals(wallet.walletCoins);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, login, password, walletCoins);
    }

    @Override
    public String toString() {
        return "Wallet{" +
                "id='" + id + '\'' +
                ", login='" + login + '\'' +
                ", password='" + password + '\'' +
                ", walletCoins=" + walletCoins +
                '}';
    }
}
