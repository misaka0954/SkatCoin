import net.MiningServer;

public class SkatCoinServer {
    //public static ArrayList<Wallet> wallets = new ArrayList<>();
    //public static ArrayList<BaseCoin> coins = new ArrayList<>();
    public static MiningServer server;
    public static void main(String[] args){
        loadWalletsData();
        loadCoins();
        server = new MiningServer();
    }

    public static void loadWalletsData(){

    }

    public static void loadCoins(){

    }
}
