import java.util.ArrayList;

public class Wallet {
    public String id;
    public String login;
    public String password;
    public ArrayList<BaseCoin> walletCoins;

    public Wallet(String id, String login,String password){
        this.id =id;
        this.login=login;
        this.password=password;
        walletCoins=new ArrayList<>();
    }
}