package uwu.misaka;

import java.util.Objects;

public class BaseCoin {
    public String id;
    public String walletId;

    public BaseCoin(String id) {
        this(id, "0");
    }

    public BaseCoin(String coin, String wallet) {
        id = coin;
        walletId = wallet;
    }

    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BaseCoin baseCoin = (BaseCoin)o;
        return id.equals(baseCoin.id) && walletId.equals(baseCoin.walletId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, walletId);
    }

    @Override
    public String toString() {
        return "BaseCoin{" +
                "id='" + id + '\'' +
                ", walletId='" + walletId + '\'' +
                '}';
    }
}
