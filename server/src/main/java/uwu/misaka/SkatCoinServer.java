package uwu.misaka;

import uwu.misaka.net.MiningServer;

import java.util.*;

public class SkatCoinServer {
    public static List<Wallet> wallets = new ArrayList<>();
    public static List<BaseCoin> coins = new ArrayList<>();
    public static WalletDao walletDao;
    public static BaseCoinDao baseCoinDao;
    public static MiningServer server;

    public static void main(String[] args){
        walletDao = new WalletDao();
        baseCoinDao = new BaseCoinDao();
        wallets = walletDao.getAll();
        coins = baseCoinDao.getAll();

        server = new MiningServer();
    }
}
