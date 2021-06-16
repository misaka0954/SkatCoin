package uwu.misaka;

import uwu.misaka.net.MiningServer;

import java.util.*;

public class SkatCoinServer {
    public static List<Wallet> activeWallets = new ArrayList<>();
    public static WalletDao walletDao;
    public static BaseCoinDao baseCoinDao;
    public static MiningServer server;

    public static void main(String[] args){
        walletDao = new WalletDao();
        baseCoinDao = new BaseCoinDao();

        server = new MiningServer();
    }
}
