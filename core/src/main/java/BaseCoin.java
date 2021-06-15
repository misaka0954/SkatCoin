public class BaseCoin {
    public String coin_id;
    public String walletId;

    public BaseCoin(String id){
        coin_id = id;
        walletId = "0";
    }

    public BaseCoin(String coin,String wallet){
        coin_id=coin;
        walletId=wallet;
    }
}